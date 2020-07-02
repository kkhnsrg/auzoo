package edu.kokhan.auzoo.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.kokhan.auzoo.R
import edu.kokhan.auzoo.di.appModule
import org.koin.core.context.startKoin
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

private const val CAMERA_STORAGE_REQUEST_CODE = 666
private const val NECESSARY_PERMISSIONS_MSG = "Permissions are necessary for app usage"


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        fun start(context: Context) =
            context.startActivity(Intent(context, MainActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionsForAppUsage()
    }

    @AfterPermissionGranted(CAMERA_STORAGE_REQUEST_CODE)
    private fun requestPermissionsForAppUsage() {
        val perms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (EasyPermissions.hasPermissions(this, *perms)) {
            showContent()
        } else {
            permissionRequest(*perms)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        hideContent()
    }

    private fun permissionRequest(vararg perms: String) =
        EasyPermissions.requestPermissions(
            this,
            NECESSARY_PERMISSIONS_MSG,
            CAMERA_STORAGE_REQUEST_CODE,
            *perms
        )

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hideContent(){
//        appContentContainer.visibility = View.GONE
//        tv_accessDenied.visibility = View.VISIBLE
    }

    private fun showContent(){
//        appContentContainer.visibility = View.VISIBLE
//        tv_accessDenied.visibility = View.GONE
    }
}
