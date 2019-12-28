package com.kobbi.project.renamefile.view.adapter

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.project.renamefile.R
import com.kobbi.project.renamefile.listener.ClickListener
import com.kobbi.project.renamefile.utils.Utils
import com.kobbi.project.renamefile.view.model.DirViewModel
import java.io.File

class BindingAdapter private constructor() {
    companion object {

        @BindingAdapter("app:setDir", "app:setVm")
        @JvmStatic
        fun setDirectory(
            recyclerView: RecyclerView,
            items: List<File>?,
            dirVm: DirViewModel?
        ) {
            if (items != null && dirVm != null) {
                recyclerView.adapter?.run {
                    if (this is DirAdapter)
                        setItems(items)
                } ?: kotlin.run {
                    val adapter = DirAdapter(items).apply {
                        setOnClickListener(object : ClickListener {
                            override fun onItemClick(position: Int, view: View) {
                                view.context.applicationContext?.let {
                                    dirVm.clickItem(position)
                                }
                            }
                        })
                    }
                    recyclerView.adapter = adapter
                }
            }
        }

        @BindingAdapter("app:setImg", "app:getFile")
        @JvmStatic
        fun setImg(view: ImageView, img: Bitmap?, file: File) {
            with(view) {
                if (file.isDirectory)
                    setImageResource(R.drawable.baseline_folder_white_48)
                else {
                    file.run {
                        when {
                            Utils.isPictureFile(this) ->
                                setImageBitmap(img)
                            Utils.isZipFile(this) ->
                                setImageResource(R.drawable.icons8_archive_folder_96)
                            else ->
                                setImageResource(R.drawable.baseline_insert_drive_file_white_48)
                        }
                    }
                }
            }
        }

        @BindingAdapter("app:getFileSize")
        @JvmStatic
        fun getFileSize(view: TextView, file: File?) {
            file?.run {
                if (!isDirectory) {
                    var b = length()
                    var count = 0
                    while (b >= 1024) {
                        b /= 1024
                        count++
                    }
                    val suffix = when (count) {
                        1 -> "KB"
                        2 -> "MB"
                        3 -> "GB"
                        else -> "byte"
                    }
                    view.text = "$b $suffix"
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.INVISIBLE
                }
            }
        }

        @BindingAdapter("app:setFiles", "app:setVm")
        @JvmStatic
        fun setPath(
            recyclerView: RecyclerView,
            items: String?,
            dirVm: DirViewModel?
        ) {
            if (items != null && dirVm != null)
                recyclerView.adapter?.run {
                    if (this is PathAdapter) {
                        setItems(items.split('/'))
                    }
                }?: kotlin.run {
                    val adapter = PathAdapter(items.split('/')).apply {
                        clickListener = object : ClickListener {
                            override fun onItemClick(position: Int, view: View) {
                                view.context.applicationContext?.let {
                                    dirVm.clickItem(position)
                                }
                            }
                        }
                    }
                    recyclerView.adapter = adapter
                }
        }
    }
}