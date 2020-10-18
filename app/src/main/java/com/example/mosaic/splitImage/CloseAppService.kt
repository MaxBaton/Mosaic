package com.example.mosaic.splitImage

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.mosaic.DeleteCache

class CloseAppService: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        DeleteCache.deleteCache(activity = null,application = application)
        stopSelf()
    }
}