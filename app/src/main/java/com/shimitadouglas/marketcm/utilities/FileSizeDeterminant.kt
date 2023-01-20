package com.shimitadouglas.marketcm.utilities

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

class FileSizeDeterminant(val context: Context) {

     private val applicationContext=context

    fun funGetSize(uriPath: Uri?): Long {
        //code begins
        val cursor =
            uriPath?.let { applicationContext.contentResolver.query(it, null, null, null, null) }
        if (cursor != null) {
            val intSizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            val floatImageSize: Long = cursor.getLong(intSizeIndex)
            cursor.close()
            return floatImageSize
        }
        return 0
        //code ends
    }
}