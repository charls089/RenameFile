package com.kobbi.project.renamefile.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kobbi.project.renamefile.R
import com.kobbi.project.renamefile.databinding.ActivityMainBinding
import com.kobbi.project.renamefile.utils.BackPressedCloser
import com.kobbi.project.renamefile.utils.Utils
import com.kobbi.project.renamefile.view.model.DirViewModel

class MainActivity : AppCompatActivity() {
    private val mBackPressedCloser by lazy { BackPressedCloser(this) }
    private val mDirViewModel: DirViewModel by lazy {
        ViewModelProviders.of(this)[DirViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).run {
            dirVm = mDirViewModel.apply {
                currentPath.observe(this@MainActivity, Observer {
                    Utils.setPath(applicationContext, it)
                })
            }
            lifecycleOwner = this@MainActivity
        }
    }

    override fun onBackPressed() {
        mDirViewModel.run {
            if (isRootPath())
                mBackPressedCloser.onBackPressed()
            else
                goToPrevPath()
        }
    }
}
