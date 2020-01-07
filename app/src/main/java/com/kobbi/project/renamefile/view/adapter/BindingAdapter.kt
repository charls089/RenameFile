package com.kobbi.project.renamefile.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.project.renamefile.R
import com.kobbi.project.renamefile.listener.ClickListener
import com.kobbi.project.renamefile.listener.LongClickListener
import com.kobbi.project.renamefile.utils.Utils
import com.kobbi.project.renamefile.view.model.DirViewModel
import java.io.File

class BindingAdapter private constructor() {
    companion object {

        @BindingAdapter("app:setDir", "app:setVm", "app:setSelectMode", "app:setSelectedPositions")
        @JvmStatic
        fun setDirectory(
            recyclerView: RecyclerView,
            items: List<File>?,
            dirVm: DirViewModel?,
            selectMode: DirViewModel.SelectMode?,
            positions: List<Int>?
        ) {
            Log.e("####", "setDirectory() --> items : $items")
            if (items != null && dirVm != null) {
                recyclerView.adapter?.run {
                    if (this is DirAdapter) {
                        val mode = selectMode ?: DirViewModel.SelectMode.NORMAL
                        if (setItems(items) || setSelectMode(mode))
                            if (items.isEmpty())
                                notifyDataSetChanged()
                            else
                                notifyItemRangeChanged(0, items.size)
                        if (positions != null)
                            setSelectedPositions(positions)
                    }
                } ?: kotlin.run {
                    val adapter = DirAdapter(items).apply {
                        clickListener = object : ClickListener {
                            override fun onItemClick(position: Int, view: View) {
                                dirVm.clickItem(position)
                            }
                        }

                        longClickListener = object : LongClickListener {
                            override fun onItemLongClick(position: Int, view: View) {
                                dirVm.longClickItem(position)
                            }
                        }
                        setSelectMode(
                            selectMode ?: DirViewModel.SelectMode.NORMAL
                        )
                    }
                    recyclerView.adapter = adapter.apply {
                        notifyDataSetChanged()
                    }
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

        @BindingAdapter("app:setRefresh")
        @JvmStatic
        fun setRefresh(view: RecyclerView, refreshList: List<Int>?) {
            Log.e("####", "setRefresh() -->refreshList : $refreshList")
            view.adapter?.run {
                if (refreshList.isNullOrEmpty())
                    notifyDataSetChanged()
                else
                    refreshList.forEach { position ->
                        notifyItemChanged(position)
                    }
            }
        }

        @BindingAdapter("app:setSelectMode")
        @JvmStatic
        fun setSelectMode(view: View, mode: DirViewModel.SelectMode?) {
            when (mode) {
                DirViewModel.SelectMode.MULTIPLE -> {
                    view.visibility = View.VISIBLE
                }
                else -> {
                    view.visibility = View.GONE
                    if (view is CheckBox)
                        view.isChecked = false
                }
            }
        }

        @BindingAdapter("app:setFileMoveMode")
        @JvmStatic
        fun setFileMoveMode(view: View, mode: DirViewModel.SelectMode?) {
            when (mode) {
                DirViewModel.SelectMode.MOVE, DirViewModel.SelectMode.COPY-> {
                    view.visibility = View.VISIBLE
                }
                else -> {
                    view.visibility = View.GONE
                }
            }
        }

        @BindingAdapter("app:setMoveText")
        @JvmStatic
        fun setMoveText(button: Button, mode: DirViewModel.SelectMode?) {
            when (mode) {
                DirViewModel.SelectMode.MOVE -> {
                    button.text = "여기로 이동"
                }
                DirViewModel.SelectMode.COPY -> {
                    button.text = "여기로 복사"
                }
                else -> {
                    button.text = ""
                }
            }
        }

        @BindingAdapter("app:setOpenDialog")
        @JvmStatic
        fun setOpenDialog(view: View, isOpen: Boolean) {
            if (isOpen) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
                (view.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).run {
                    hideSoftInputFromWindow(view.windowToken, 0)
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
                        setItems(setItemPath(items, dirVm.rootPath))
                    }
                } ?: kotlin.run {
                    val adapter = PathAdapter(setItemPath(items, dirVm.rootPath)).apply {
                        clickListener = object : ClickListener {
                            override fun onItemClick(position: Int, view: View) {
                                view.context.applicationContext?.let {
                                    dirVm.clickPath(position)
                                }
                            }
                        }
                    }
                    recyclerView.adapter = adapter
                }
        }

        private fun setItemPath(path: String, rootPath: String?): List<String> {
            var replacePath = ""
            if (rootPath != null && path.contains(rootPath)) {
                replacePath = path.replace(rootPath, "내부저장소")
            }
            return replacePath.split('/')
        }
    }
}