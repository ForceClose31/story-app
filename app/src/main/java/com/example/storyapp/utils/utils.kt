package com.example.storyapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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


fun compressImage(file: File, maxSize: Long): File {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

    var quality = 100
    var byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

    while (byteArrayOutputStream.size() > maxSize && quality > 10) {
        byteArrayOutputStream = ByteArrayOutputStream()
        quality -= 5
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
    }

    val compressedFile = File(file.parent, "compressed_${file.name}")
    try {
        val fileOutputStream = FileOutputStream(compressedFile)
        fileOutputStream.write(byteArrayOutputStream.toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return compressedFile
}
