package com.example.projectskripsi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper untuk mengatur reminder masa berlaku SIM.
 *
 * Fitur:
 * - Reminder H-30, H-7, H-3, H-1 sebelum SIM expired
 * - Menggunakan exact alarm (Android 12+ aware)
 * - Test notification tanpa exact alarm (aman tanpa permission)
 */
public class ReminderAlarmHelper {

    // ===============================
    // PUBLIC API
    // ===============================

    /**
     * Menjadwalkan reminder SIM sebelum masa berlaku habis.
     *
     * @param context            context aplikasi
     * @param masaBerlakuMillis  waktu expired SIM (millis)
     * @param namaPemilik        nama pemilik SIM
     */
    public static void scheduleSimReminders(
            Context context,
            long masaBerlakuMillis,
            String namaPemilik
    ) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        // Android 12+ butuh izin exact alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !alarmManager.canScheduleExactAlarms()) {
            // Jangan crash, skip scheduling
            return;
        }

        // Reminder H-30, H-7, H-3, H-1
        int[] daysBefore = {30, 7, 3, 1};

        for (int day : daysBefore) {
            scheduleSingleReminder(
                    context,
                    alarmManager,
                    masaBerlakuMillis,
                    day,
                    namaPemilik
            );
        }
    }

    /**
     * Reminder test (tanpa exact alarm, aman tanpa permission).
     * Berguna untuk testing notifikasi.
     */
    public static void scheduleTestReminder(Context context) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10); // 10 detik dari sekarang

        Intent intent = new Intent(context, SimReminderReceiver.class);
        intent.putExtra("title", "TEST NOTIFICATION");
        intent.putExtra("message", "Jika ini muncul, sistem notif OK ✅");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                999999,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Non-exact alarm → tidak butuh permission khusus
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    // ===============================
    // INTERNAL HELPERS
    // ===============================

    /**
     * Menjadwalkan satu reminder SIM.
     */
    private static void scheduleSingleReminder(
            Context context,
            AlarmManager alarmManager,
            long expiryMillis,
            int daysBefore,
            String namaPemilik
    ) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(expiryMillis);
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore);

        // Skip kalau waktu reminder sudah lewat
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) return;

        Intent intent = new Intent(context, SimReminderReceiver.class);
        intent.putExtra("title", "SIM Akan Expired");
        intent.putExtra(
                "message",
                "SIM atas nama " + namaPemilik +
                        " akan habis dalam " + daysBefore + " hari"
        );

        // Request code unik per hari
        int requestCode = (int) (expiryMillis + daysBefore);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    // ===============================
    // GENERIC DOCUMENT REMINDER
    // ===============================
    public static void scheduleDocumentReminder(
            Context context,
            long targetMillis,
            String title
    ) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        // Android 12+ exact alarm check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !alarmManager.canScheduleExactAlarms()) {
            return;
        }

        int[] daysBefore = {7, 3, 1};

        for (int day : daysBefore) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(targetMillis);
            calendar.add(Calendar.DAY_OF_YEAR, -day);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) continue;

            Intent intent = new Intent(context, SimReminderReceiver.class);
            intent.putExtra("title", "Reminder Dokumen");
            intent.putExtra(
                    "message",
                    title + " akan jatuh tempo dalam " + day + " hari"
            );

            int requestCode = (int) (targetMillis + day);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    // ===============================
    // GENERIC REMINDER (SIM / STNK / REMINDER)
    // ===============================

    /**
     * Schedule generic reminders with custom offsets.
     * Returns requestCodes that MUST be saved to Firestore
     * so they can be cancelled on delete.
     */
    public static List<Integer> scheduleGenericReminders(
            Context context,
            long targetMillis,
            int[] offsets,
            String title,
            String message
    ) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        List<Integer> requestCodes = new ArrayList<>();
        if (alarmManager == null) return requestCodes;

        // Android 12+ exact alarm check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !alarmManager.canScheduleExactAlarms()) {
            return requestCodes;
        }

        for (int day : offsets) {

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(targetMillis);
            cal.add(Calendar.DAY_OF_YEAR, -day);

            if (cal.getTimeInMillis() < System.currentTimeMillis()) continue;

            int requestCode = (int) (targetMillis + day);
            requestCodes.add(requestCode);

            Intent intent = new Intent(context, SimReminderReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("message", message + " (" + day + " hari)");

            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    pi
            );
        }

        return requestCodes;
    }

    /**
     * Cancel reminders using stored requestCodes.
     * MUST be called before deleting document.
     */
    public static void cancelReminders(
            Context context,
            List<Integer> requestCodes
    ) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null || requestCodes == null) return;

        for (int code : requestCodes) {
            Intent intent = new Intent(context, SimReminderReceiver.class);

            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    code,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.cancel(pi);
        }
    }

    public static void scheduleImmediateReminder(
            Context context,
            String title,
            String message
    ) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        long triggerAt = System.currentTimeMillis() + 10_000; // 10 detik

        Intent intent = new Intent(context, SimReminderReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        int requestCode = (int) (System.currentTimeMillis() / 1000);

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pi
        );
    }

}