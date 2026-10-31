package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseConfig {
    const val API_KEY = "AIzaSyAwiMHd5xaq7g40MMtdw-SNF0PANRFCOvA"
    const val AUTH_DOMAIN = "caht-box.firebaseapp.com"
    const val DATABASE_URL = "https://caht-box-default-rtdb.firebaseio.com"
    const val PROJECT_ID = "caht-box"
    const val STORAGE_BUCKET = "caht-box.firebasestorage.app"
    const val MESSAGING_SENDER_ID = "324693596002"
    const val APP_ID = "1:324693596002:android:f5d74f3990e07b5ac3be3b"

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApiKey(API_KEY)
                    .setApplicationId(APP_ID)
                    .setDatabaseUrl(DATABASE_URL)
                    .setProjectId(PROJECT_ID)
                    .setStorageBucket(STORAGE_BUCKET)
                    .setGcmSenderId(MESSAGING_SENDER_ID)
                    .build()
                FirebaseApp.initializeApp(context, options)
                Log.d("FirebaseConfig", "Firebase initialized for project: $PROJECT_ID")
            } else {
                Log.d("FirebaseConfig", "Firebase already initialized")
            }
        } catch (e: Exception) {
            Log.w("FirebaseConfig", "Firebase init notification: ${e.message}")
        }
    }
}
