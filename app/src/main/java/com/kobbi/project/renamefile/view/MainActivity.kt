package com.kobbi.project.renamefile.view

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kobbi.project.renamefile.R
import com.kobbi.project.renamefile.databinding.ActivityMainBinding
import com.kobbi.project.renamefile.utils.BackPressedCloser
import com.kobbi.project.renamefile.utils.Utils
import com.kobbi.project.renamefile.view.model.DirViewModel
import java.io.File

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

                clickEdit.observe(this@MainActivity, Observer {
                    Log.e("####", "clickEdit Button")
                    //dialog
                    AlertDialog.Builder(this@MainActivity).run {
                        setTitle("파일 확장자 변경")
                        setMessage("정말 변경 하시겠습니까?")
                        setPositiveButton("예") { dialog, _ ->
                            mDirViewModel.editFileExtension()
                            dialog.dismiss()
                        }
                        setNegativeButton("아니요") { dialog, _ ->
                            dialog.cancel()
                        }
                        setCancelable(false)
                        show()
                    }
                })

                clickSend.observe(this@MainActivity, Observer {
                    Log.e("####", "clickSend Button")
                    //TODO G-Mail 공유하기
                    Toast.makeText(this@MainActivity, "아직 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
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
