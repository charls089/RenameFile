package com.kobbi.project.renamefile.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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

                clickEdit.observe(this@MainActivity, Observer {
                    Log.e("####", "clickEdit Button")
                    //dialog
                    AlertDialog.Builder(this@MainActivity).run {
                        setTitle(R.string.dialog_edit_title)
                        setMessage(R.string.dialog_edit_message)
                        setPositiveButton(R.string.dialog_yes) { dialog, _ ->
                            mDirViewModel.editFileExtension()
                            dialog.dismiss()
                        }
                        setNegativeButton(R.string.dialog_no) { dialog, _ ->
                            dialog.cancel()
                        }
                        setCancelable(false)
                        show()
                    }
                })

                clickSend.observe(this@MainActivity, Observer {
                    Log.e("####", "clickSend Button")
                    val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                        type = "plain/text"
                        val uriList = ArrayList<Uri>().apply {
                            it?.forEach {
                                val uri =
                                    FileProvider.getUriForFile(
                                        applicationContext, "$packageName.file.provider", it
                                    )
                                Log.e("####", "uri : $uri")
                                add(uri)
                            }
                        }
                        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
                    }
                    startActivity(sendIntent)
                })

                clickCreateFolder.observe(this@MainActivity, Observer {
                    Log.e("####", "clickCreateFolder Button")
                })

                clickDelete.observe(this@MainActivity, Observer {
                    Log.e("####", "clickDelete Button")
                    AlertDialog.Builder(this@MainActivity).run {
                        setTitle(R.string.dialog_delete_files_title)
                        setMessage(R.string.dialog_delete_files_message)
                        setPositiveButton(R.string.dialog_yes) { dialog, _ ->
                            mDirViewModel.removeFiles()
                            dialog.dismiss()
                        }
                        setNegativeButton(R.string.dialog_no) { dialog, _ ->
                            dialog.cancel()
                        }
                        setCancelable(false)
                        show()
                    }
                })

                clickCopy.observe(this@MainActivity, Observer {
                    Log.e("####", "clickCopy Button")
                })

                clickMove.observe(this@MainActivity, Observer {
                    Log.e("####", "clickMove Button")
                })

                clickInfo.observe(this@MainActivity, Observer {
                    Log.e("####", "clickInfo Button")
                    Toast.makeText(applicationContext, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show()
                })
            }
            lifecycleOwner = this@MainActivity
        }
    }

    override fun onBackPressed() {
        mDirViewModel.run {
            when {
                this.selectMode.value == DirViewModel.SelectMode.MULTIPLE -> {
                    resetMode()
                }
                isRootPath() -> mBackPressedCloser.onBackPressed()
                else -> goToPrevPath()
            }
        }
    }
}
