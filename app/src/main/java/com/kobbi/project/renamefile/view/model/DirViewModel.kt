package com.kobbi.project.renamefile.view.model

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kobbi.project.renamefile.utils.SingleLiveEvent
import com.kobbi.project.renamefile.utils.Utils
import java.io.File
import java.io.FileFilter
import java.util.*
import kotlin.Comparator
import kotlin.concurrent.schedule

class DirViewModel : ViewModel() {
    enum class SelectMode {
        NORMAL, MULTIPLE, MOVE, COPY
    }

    val selectMode: LiveData<SelectMode> get() = _selectMode
    val selectedPositions: LiveData<List<Int>> get() = _selectedPositions
    val currentItems: LiveData<List<File>> get() = _currentItems
    val currentPath: LiveData<String> get() = _currentPath

    val clickEdit: SingleLiveEvent<Any> = SingleLiveEvent()
    val clickSend: SingleLiveEvent<List<File>> = SingleLiveEvent()
    val clickDelete: SingleLiveEvent<Any> = SingleLiveEvent()
    val clickInfo: SingleLiveEvent<File> = SingleLiveEvent()

    val folderName: MutableLiveData<String> = MutableLiveData()
    val isCreateNewFolderOpen: LiveData<Boolean> get() = _isCreateNewFolderOpen

    private val _isCreateNewFolderOpen: MutableLiveData<Boolean> = MutableLiveData()
    private val _selectedPositions: MutableLiveData<List<Int>> = MutableLiveData()
    private val _currentPath: MutableLiveData<String> = MutableLiveData()
    private val _currentItems: MutableLiveData<List<File>> = MutableLiveData()
    private val _selectMode: MutableLiveData<SelectMode> = MutableLiveData()

    private var mSelectedItems: List<File>? = null
    val rootPath: String? = Environment.getExternalStorageDirectory().absolutePath

    init {
        setItems(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        )
        _selectMode.postValue(SelectMode.NORMAL)
    }

    fun isRootPath(): Boolean {
        return _currentPath.value == rootPath
    }

    fun resetMode() {
        _selectMode.postValue(SelectMode.NORMAL)
        _selectedPositions.postValue(listOf())
        _isCreateNewFolderOpen.postValue(false)
        clickInfo.postValue(null)
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
            file.renameTo(File(renameFilePath))
        }
        _currentItems.postValue(listOf())
        Timer().schedule(300) {
            setItems(_currentPath.value)
        }
    }

    fun clickItem(position: Int) {
        (_selectMode.value ?: SelectMode.NORMAL).let { mode ->
            if (mode == SelectMode.MULTIPLE) {
                val selectedPositions = mutableListOf<Int>().apply {
                    _selectedPositions.value?.run {
                        addAll(this)
                    }
                }
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove(position)
                } else {
                    selectedPositions.add(position)
                }
                _selectedPositions.postValue(selectedPositions)
            } else
                clickPath(position)
        }
    }

    fun clickPath(position: Int) {
        _currentItems.value?.get(position)?.let { file ->
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

    fun longClickItem(position: Int) {
        if (_selectMode.value == SelectMode.MULTIPLE) {
            clickItem(position)
        } else {
            _selectMode.postValue(SelectMode.MULTIPLE)
            _selectedPositions.postValue(listOf(position))
        }
    }

    fun clickEdit() {
        clickEdit.call()
    }

    fun clickSend() {
        clickSend.call(_currentItems.value)
    }

    fun clickCreateFolder() {
        _isCreateNewFolderOpen.postValue(true)
    }

    fun clickMove() {
        _selectMode.postValue(SelectMode.MOVE)
        setSelectedItems()
    }

    fun clickCopy() {
        _selectMode.postValue(SelectMode.COPY)
        setSelectedItems()
    }

    fun clickDelete() {
        clickDelete.call()
    }

    fun clickInfo() {
        val file = _selectedPositions.value?.lastOrNull()?.let { position ->
            _currentItems.value?.get(position)
        }
        clickInfo.call(file)
    }

    fun clickCancel() {
        _isCreateNewFolderOpen.postValue(false)
    }

    fun createNewFolder() {
        _isCreateNewFolderOpen.postValue(false)
        _currentPath.value?.let { path ->
            val name = folderName.value ?: "새폴더"
            File("$path/$name").mkdirs()
            setItems(path)
            folderName.postValue("")
        }
    }

    fun removeFiles() {
        _selectedPositions.value?.let { positions ->
            positions.forEach {
                _currentItems.value?.get(it)?.run {
                    removeDir(this)
                }
            }
        }
        resetMode()
        setItems(_currentPath.value)
    }

    fun moveFiles() {
        val destPath = _currentPath.value
        val mode = _selectMode.value
        mSelectedItems?.forEach {
            val filePath = "$destPath/${it.name}"
            if (!File(filePath).exists())
                when (mode) {
                    SelectMode.MOVE -> {
                        it.renameTo(File(filePath))
                    }
                    SelectMode.COPY -> {
                        it.copyTo(File(filePath))
                    }
                    else -> {
                        //Nothing.
                    }
                }
        }
        resetMode()
        setItems(_currentPath.value)
    }

    fun clickAll() {
        _currentItems.value?.let { files ->
            _selectedPositions.value?.let { positions ->
                if (positions.size != files.size) {
                    val tmpFiles = mutableListOf<Int>()
                    for (i in files.indices)
                        tmpFiles.add(i)
                    _selectedPositions.postValue(tmpFiles)
                } else {
                    _selectedPositions.postValue(listOf())
                }
            }
        }
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
        }
    }

    private fun setSelectedItems() {
        mSelectedItems = _currentItems.value?.filterIndexed { index, _ ->
            _selectedPositions.value?.contains(index) ?: false
        }
    }

    private fun removeDir(file: File) {
        file.listFiles()?.forEach {
            if (it.isDirectory)
                removeDir(it)
            else
                it.delete()
        } ?: file.delete()
    }
}