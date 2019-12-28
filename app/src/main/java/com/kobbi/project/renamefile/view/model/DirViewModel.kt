package com.kobbi.project.renamefile.view.model

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.project.renamefile.utils.SingleLiveEvent
import com.kobbi.project.renamefile.utils.Utils
import java.io.File
import java.io.FileFilter

class DirViewModel(application: Application) : AndroidViewModel(application) {
    val currentItems: LiveData<List<File>> get() = _currentItems
    val currentPath: LiveData<String> get() = _currentPath
    val clickEdit: SingleLiveEvent<Any> = SingleLiveEvent()
    val clickSend: SingleLiveEvent<Any> = SingleLiveEvent()

    private val _currentPath: MutableLiveData<String> = MutableLiveData()
    private val _currentItems: MutableLiveData<List<File>> = MutableLiveData()

    val rootPath = Environment.getExternalStorageDirectory().absolutePath

    init {
//        val prevCurrentPath = Utils.getPath(application)
        Log.e("####", "rootPath : $rootPath")
        setItems(
//            if (prevCurrentPath.isEmpty())
            rootPath
//            else
//                prevCurrentPath
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

    fun editFileExtension() {
        _currentItems.value?.forEach { file ->
            val fileName = file.nameWithoutExtension
            val editExtension = file.extension.run {
                if (this.length > 1)
                    dropLast(1)
                else
                    this
            }
            val renameFilePath = "${file.parent}/$fileName.$editExtension"
            Log.e("####", "renameFilePath : $renameFilePath")
            file.renameTo(File(renameFilePath))
        }
        setItems(_currentPath.value)
    }

    fun clickItem(position: Int) {
        _currentItems.value?.get(position)?.let { file ->
            Log.e("####", "clickItem() --> position : $position, file.path : ${file.path}")
            when {
                Utils.isDirectory(file) -> {
                    setItems(file.path)
                }
                else -> {
                    //Nothing.
                }
            }
        }
    }

    fun clickEdit() {
        clickEdit.call()
    }

    fun clickSend() {
        clickSend.call()
    }

    private fun setItems(path: String?) {
        path?.run {
            val f = File(path)
            val file = if (f.isDirectory) f else f.parentFile
            val filterList = file.listFiles(FileFilter {
                !it.name.startsWith(".")
            })?.run {
                sortWith(Comparator<File> { p0, p1 ->
                    when {
                        p0.isDirectory && p1.isFile ->
                            -1
                        p0.isDirectory && p1.isDirectory ->
                            0
                        p0.isFile && p1.isFile ->
                            0
                        else ->
                            1
                    }
                })
                toList()
            }
            _currentItems.postValue(filterList)
            _currentPath.postValue(file.path)
        } ?: kotlin.run {
            Log.e("####", "path is null")
        }
    }
}