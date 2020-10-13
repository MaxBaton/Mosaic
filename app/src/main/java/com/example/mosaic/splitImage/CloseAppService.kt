package com.example.mosaic.splitImage

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.mosaic.DeleteCache
import com.example.mosaic.SplitActivity

class CloseAppService: Service() {
    //var activity: Activity? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
//        val activity = rootIntent!!.getParcelableExtra<SplitActivity>("activity")

//        android.os.Debug.waitForDebugger()
//        Log.d("service","service is started")

        DeleteCache.deleteCache(activity = null,application = application)
        stopSelf()
    }
}