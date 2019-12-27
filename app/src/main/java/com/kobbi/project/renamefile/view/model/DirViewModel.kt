package com.kobbi.project.renamefile.view.model

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.project.renamefile.utils.Utils
import java.io.File

class DirViewModel(application: Application):AndroidViewModel(application) {
    val fileList:LiveData<List<File>> get() = _fileList

    private val _rootPath = Environment.getRootDirectory().toString()
    private val _currentPath: MutableLiveData<String> = MutableLiveData()
    private val _fileList:MutableLiveData<List<File>> = MutableLiveData()

    init {
        _currentPath.postValue(
            Utils.getPath(application)
        )

    }
}