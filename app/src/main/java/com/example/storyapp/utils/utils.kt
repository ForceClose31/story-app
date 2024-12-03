package com.example.storyapp.utils

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtil {
    fun getFile(context: Context, uri: Uri): File {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val path = cursor?.getString(cursor.getColumnIndexOrThrow("_data"))
        cursor?.close()
        return File(path ?: "")
    }

    fun createImageFile(context: Context): File {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, "story_${System.currentTimeMillis()}.jpg")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }



}
