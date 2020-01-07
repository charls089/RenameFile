package com.kobbi.project.renamefile.view

import android.os.Bundle
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.kobbi.project.renamefile.R
import com.kobbi.project.renamefile.databinding.DialogFileInfoBinding
import java.io.File

class FileInfoDialog(private val mFile: File) : DialogFragment() {
    companion object {
        const val TAG = "FileInfoDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            DataBindingUtil.inflate<DialogFileInfoBinding>(
                inflater,
                R.layout.dialog_file_info,
                container,
                false
            ).run {
                file = mFile
                lifecycleOwner = this@FileInfoDialog
                root
            }
        } catch (e: InflateException) {
            null
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes?.apply {
            width = LinearLayout.LayoutParams.MATCH_PARENT
            height = LinearLayout.LayoutParams.WRAP_CONTENT
        }
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
    }
}