package com.example.rtc_android

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.rtc_android.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        val perList = mutableListOf(
            Manifest.permission.RECORD_AUDIO
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            perList.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        PermissionX.init(this)
            .permissions(perList)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    resources.getString(R.string.permission_request_reason),
                    resources.getString(R.string.positive),
                    resources.getString(R.string.nagative)
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    resources.getString(R.string.forward_to_setting_permission),
                    resources.getString(R.string.positive),
                    resources.getString(R.string.nagative)
                )
            }
            .request { allGranted, _, _ ->
                if (allGranted.not()) {
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.some_permission_not_grante),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) return true
        return super.onKeyDown(keyCode, event)
    }
}