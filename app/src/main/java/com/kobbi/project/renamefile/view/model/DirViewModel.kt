package com.kobbi.project.renamefile.view.model

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.project.renamefile.utils.Utils
import java.io.File
import java.io.FileFilter

class DirViewModel(application: Application) : AndroidViewModel(application) {
    val currentItems: LiveData<List<File>> get() = _currentItems
    val currentPath: LiveData<String> get() = _currentPath

    private val _currentPath: MutableLiveData<String> = MutableLiveData()
    private val _currentItems: MutableLiveData<List<File>> = MutableLiveData()

    private val rootPath = Environment.getRootDirectory().toString()

    init {
        val prevCurrentPath = Utils.getPath(application)
        setItems(
            if (prevCurrentPath.isEmpty())
                rootPath
            else
                prevCurrentPath
        )
    }

    fun isRootPath(): Boolean {
        return _currentPath.value == rootPath
    }

    fun goToPrevPath() {
        val currentPath = _currentPath.value
        currentPath?.let {
            val prevPath = currentPath.substringBeforeLast('/')
            setItems(prevPath)
        }
    }

    fun clickItem(position: Int) {
        _currentItems.value?.get(position)?.let {file ->
            Log.e("####","clickItem() --> position : $position, file.path : ${file.path}")
            when {
                Utils.isDirectory(file) -> {
                    setItems(file.path)
                }
                else -> {
                    file.renameTo(File(file.path.dropLast(1)))
                }
            }
        }
    }

    private fun setItems(path: String) {
        val f = File(path)
        val file = if (f.isDirectory) f else f.parentFile
        val filterList = file.listFiles(FileFilter {
            Utils.isDirectory(it) || Utils.isZipFile(it) || Utils.isPictureFile(it)
        })?.toList()?.sorted()
        _currentItems.postValue(filterList)
        _currentPath.postValue(file.path)
    }
}