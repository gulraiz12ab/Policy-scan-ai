package app.dev.policyscanai.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Utility functions for handling file metadata from URIs.
 */

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            if (result != null && cut != null) {
                result = result.substring(cut + 1)
            }
        }
    }
    return result ?: "unknown_file"
}

fun getFileType(context: Context, uri: Uri): String {
    val mimeType = context.contentResolver.getType(uri)
    return when {
        mimeType?.startsWith("image/") == true -> "IMAGE"
        mimeType == "application/pdf" -> "PDF"
        else -> "UNKNOWN"
    }
}

fun getFileSizeKb(context: Context, uri: Uri): Long {
    var fileSize: Long = 0
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (index != -1) {
                    fileSize = cursor.getLong(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    return fileSize / 1024
}
