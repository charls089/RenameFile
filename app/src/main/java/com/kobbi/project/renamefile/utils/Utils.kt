package com.kobbi.project.renamefile.utils

import android.content.Context
import android.graphics.BitmapFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Utils private constructor() {
    companion object {
        @JvmStatic
        fun isPictureFile(file: File): Boolean {
            return file.extension.endsWith("png") ||
                    file.extension.endsWith("jpg") ||
                    file.extension.endsWith("gif")
        }

        @JvmStatic
        fun isZipFile(file: File): Boolean {
            return file.extension.endsWith("zip")
        }

        @JvmStatic
        fun isDirectory(file: File): Boolean {
            return (file.isDirectory && !file.name.startsWith('.'))
        }

        @JvmStatic
        fun getPath(context: Context) =
            SharedPrefHelper.getString(context, SharedPrefHelper.KEY_LAST_OPEN_FILE_PATH)

        @JvmStatic
        fun setPath(context: Context, path: String) {
            SharedPrefHelper.setString(context, SharedPrefHelper.KEY_LAST_OPEN_FILE_PATH, path)
        }

        @JvmStatic
        fun setPage(context: Context, position: Int) {
            SharedPrefHelper.setInt(context, SharedPrefHelper.KEY_LAST_VIEW_FILE_PAGE, position)
        }

        @JvmStatic
        fun getPage(context: Context) =
            SharedPrefHelper.getInt(context, SharedPrefHelper.KEY_LAST_VIEW_FILE_PAGE)

        @JvmStatic
        fun getBitmap(file: File, reqWidth: Int, regHeight: Int) =
            if (isPictureFile(file)) {
                BitmapFactory.Options().run {
                    inJustDecodeBounds = true
                    BitmapFactory.decodeFile(file.path, this)
                    var inSampleSize = 1
                    if (outWidth > reqWidth || outHeight > regHeight) {
                        inSampleSize = 2
                        while (outWidth / inSampleSize > reqWidth && outHeight / inSampleSize > regHeight)
                            inSampleSize *= 2
                    }
                    this.inSampleSize = inSampleSize
                    inJustDecodeBounds = false

                    BitmapFactory.decodeFile(file.path, this)
                }
            } else {
                null
            }

        @JvmStatic
        fun convertLongToDate(timeMills: Long) =
            SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault()).format(timeMills)

    }
}