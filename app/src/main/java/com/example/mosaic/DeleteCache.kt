package com.example.mosaic

import android.app.Activity
import android.app.Application
import java.io.File

object DeleteCache {
    fun deleteCache(activity: Activity? = null,application: Application) {
        val cacheDir = if (activity != null) activity.cacheDir else application.cacheDir
        deleteDir(cacheDir)
    }

    private fun deleteDir(cacheDir: File?): Boolean {
        if (cacheDir != null && cacheDir.isDirectory) {
            val children = cacheDir.list()
            children.forEach {
                val success = deleteDir(File(cacheDir,it))
                if (!success) return false
            }
            return cacheDir.delete()
        }else if (cacheDir != null && cacheDir.isFile) {
            return cacheDir.delete()
        }else {
            return false
        }
    }
}