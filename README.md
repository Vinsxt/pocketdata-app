# 📱 PocketData

**PocketData** is a native Android application developed in **Java** that provides a secure and convenient way to store personal information in the cloud. Instead of relying on physical notes or local storage, users can safely save important data and access it anytime, anywhere.

In addition to cloud storage, PocketData includes a built-in reminder system, allowing users to create reminders for important tasks, events, or personal information they don't want to forget.

---

## ✨ Features

- 🔐 Secure authentication using **Google Sign-In** and **Firebase Authentication**
- ☁️ Cloud-based personal data storage with **Firebase Firestore**
- 📁 Upload and manage files using **Firebase Storage**
- 🔢 Additional account protection with a **PIN security system**
- ⏰ Custom reminders for important events and tasks
- 📱 Clean and intuitive native Android user interface
- 🌐 Access your personal data across devices by signing into your account

---

## 🛠️ Tech Stack

| Technology | Description |
|------------|-------------|
| **Language** | Java |
| **IDE** | Android Studio |
| **Authentication** | Android Credential Manager + Firebase Authentication |
| **Database** | Firebase Firestore |
| **Cloud Storage** | Firebase Storage |
| **Platform** | Native Android |

---

## 🎯 Purpose

PocketData was developed to help users securely organize and manage personal information while ensuring it remains accessible whenever needed. By combining cloud synchronization with a reminder system, the application serves as both a personal information manager and a productivity tool.

---

## 📸 Screenshots

| Enter PIN | Home | Document Details | Select Document Type |
|:----------:|:----:|:----------------:|:--------------------:|
| <img src="screenshots/Screenshot_20260704_142822.png" width="220"> | <img src="screenshots/Screenshot_20260704_142915.png" width="220"> | <img src="screenshots/Screenshot_20260704_142933.png" width="220"> | <img src="screenshots/Screenshot_20260704_143251.png" width="220"> |

### Screen Overview

- **🔢 Enter PIN** – Unlock the application using a secure PIN before accessing your personal data.
- **🏠 Home** – View all uploaded documents stored securely in the cloud.
- **📄 Document Details** – Display detailed information and files associated with a selected document.
- **📂 Select Document Type** – Choose the category of document to upload for better organization.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio
- JDK 17 (or compatible version)
- Firebase project

### Setup

1. Clone this repository.

```bash
git clone https://github.com/Vinsxt/PocketData-app.git
```

2. Create your own Firebase project.

3. Register your Android application.

4. Enable:
   - Firebase Authentication
   - Cloud Firestore
   - Firebase Storage

5. Download your own `google-services.json`.

6. Place it inside:

```
app/google-services.json
```

7. Build and run the application.

---

## 📄 License

This project is intended for educational and portfolio purposes.
