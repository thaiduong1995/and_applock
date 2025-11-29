package com.example.myapplication.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Thinhvh on 22/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
object FileUtil {

    const val FILE_PREFIX = ".jpg"
    const val FILE_SUFFIX = "IMG_"
    const val dateFormat = "yyyy_MM_dd_HH_mm_ss"
    const val FOLDER_NAME = "CAPTURE"

    fun createFile(context: Context): File {
        val dir = File(context.filesDir, FOLDER_NAME)
        dir.mkdirs()
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        return File(dir, "${FILE_SUFFIX}${sdf.format(Date())}${FILE_PREFIX}")
    }

    fun isImageOrVideoFile(filePath: String): Boolean {
        val mimeType = getMimeType(filePath)
        return isImageMimeType(mimeType) || isVideoMimeType(mimeType)
    }

    private fun getExtensionFromFilePath(filePath: String): String? {
        val lastDotIndex = filePath.lastIndexOf(".")
        if (lastDotIndex != -1 && lastDotIndex < filePath.length - 1) {
            return filePath.substring(lastDotIndex + 1)
        }
        return null
    }

    private fun isImageMimeType(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith("image/")
    }

    private fun isVideoMimeType(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith("video/")
    }

    fun getFileUri(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentResolver: ContentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(file.absolutePath))
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
            var collection: Uri? = null
            val mimeType = getMimeType(file.path)

            collection = if (isImageMimeType(mimeType)) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            val uri = contentResolver.insert(collection, contentValues)
            uri ?: throw RuntimeException("Failed to create media file")

        } else {
            FileProvider.getUriForFile(context, "your.fileprovider.authority", file)
        }
    }

    private fun getMimeType(filePath: String): String {
        val extension = filePath.substringAfterLast('.', "")
        return if (extension.isNotEmpty()) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
                ?: "application/octet-stream"
        } else {
            "application/octet-stream"
        }
    }

    private fun getRelativePath(context: Context, absolutePath: String): String {
        val externalStorageDir = context.getExternalFilesDir(null)?.absolutePath ?: ""
        return absolutePath.removePrefix(externalStorageDir)
    }
}