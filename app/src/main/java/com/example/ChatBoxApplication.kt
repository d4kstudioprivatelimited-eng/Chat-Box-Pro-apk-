package com.example

import android.app.Application
import com.example.data.firebase.FirebaseConfig

class ChatBoxApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseConfig.init(this)
    }
}
