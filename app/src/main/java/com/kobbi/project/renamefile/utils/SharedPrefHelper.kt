package com.kobbi.project.renamefile.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper private constructor() {
    companion object {
        private const val KEY_PREFERENCE = "_Preference"

        const val KEY_LAST_VIEW_FILE_PAGE = "last_view_page"
        const val KEY_LAST_OPEN_FILE_PATH = "lat_open_file_path"

        @JvmStatic
        fun getPreference(context: Context): SharedPreferences {
            return context.applicationContext.getSharedPreferences(
                "${context.packageName}${KEY_PREFERENCE}", Context.MODE_PRIVATE
            )
        }

        @JvmStatic
        fun setInt(context: Context, key: String, value: Int) {
            getPreference(context).edit().run {
                putInt(key, value)
                apply()
            }
        }

        fun getInt(context: Context, key: String, defValue: Int = 0): Int {
            return getPreference(context).getInt(key, defValue)
        }

        @JvmStatic
        fun setString(context: Context, key: String, value: String) {
            getPreference(context).edit().run {
                putString(key, value)
                apply()
            }
        }

        @JvmStatic
        fun getString(context: Context, key: String, defValue: String = "") =
            getPreference(context).getString(key, defValue) ?: defValue
    }
}