package com.kobbi.project.renamefile.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kobbi.project.renamefile.R

class SplashActivity : AppCompatActivity() {
    companion object {
        private val NEED_PERMISSIONS =
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startService()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val needPermission: Array<String> = ArrayList<String>().run {
                NEED_PERMISSIONS.forEach {
                    val result = checkSelfPermission(it)
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        this.add(it)
                    }
                }
                this.toArray(arrayOf())
            }
            needPermission.run {
                if (isNotEmpty()) {
                    requestPermissions(
                        this,
                        REQUEST_CODE_PERMISSIONS
                    )
                } else {
                    startService()
                }
            }
        } else {
            startService()
        }
    }

    private fun startService() {
        applicationContext?.let { context ->
            startActivity(Intent(context, MainActivity::class.java))
            finish()
        }
    }
}
