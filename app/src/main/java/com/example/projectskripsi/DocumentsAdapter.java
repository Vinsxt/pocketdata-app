package com.example.projectskripsi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DocumentsAdapter
        extends RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder> {

    private final List<DocumentItem> documentList;
    private final Context context;

    // =========================
    // CONSTRUCTOR
    // =========================
    public DocumentsAdapter(Context context, List<DocumentItem> documentList) {
        this.context = context;
        this.documentList = documentList;
    }

    // =========================
    // CREATE VIEW
    // =========================
    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    // =========================
    // BIND DATA
    // =========================
    @Override
    public void onBindViewHolder(
            @NonNull DocumentViewHolder holder,
            int position
    ) {
        DocumentItem item = documentList.get(position);
        Document doc = item.document;

        holder.tvTitle.setText(doc.getTitle());
        holder.tvDesc.setText(doc.getDescription());
        holder.tvDate.setText(doc.getDate());

        // IMAGE PREVIEW
        Glide.with(holder.itemView.getContext())
                .load(doc.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(holder.ivPreview);

        // =========================
        // CLICK → DETAIL ACTIVITY
        // =========================
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DocumentDetailActivity.class);

            intent.putExtra("docId", item.firestoreId);
            intent.putExtra("type", item.type); // ✅ AMAN & BENAR

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    // =========================
    // VIEW HOLDER
    // =========================
    static class DocumentViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc, tvDate;
        ImageView ivPreview;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_doc_name);
            tvDesc  = itemView.findViewById(R.id.tv_doc_desc);
            tvDate  = itemView.findViewById(R.id.tv_doc_date);
            ivPreview = itemView.findViewById(R.id.iv_doc_preview);
        }
    }
}