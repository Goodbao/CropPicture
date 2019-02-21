package com.bao.croppicture

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import com.yanzhenjie.permission.AndPermission

class PermissionUtils(private val onCheckPermissionsListener: OnCheckPermissionsListener) {

    private var alertDialog: AlertDialog? = null

    fun checkPermission(fragment: Fragment, vararg permission: String) {
        fragment.context?.let {
            checkPermission(it, *permission)
        }
    }

    fun checkPermission(activity: Activity, vararg permission: String) {
        checkPermission(activity as Context, *permission)
    }

    /**
     * 申请权限
     */
    fun checkPermission(context: Context, vararg permission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(context)
                    .runtime()
                    .permission(*permission)
                    .onGranted {
                        //权限申请成功
                        onCheckPermissionsListener.onSuccess()
                    }
                    .onDenied {
                        //拒绝权限
                        if (AndPermission.hasAlwaysDeniedPermission(context, it)) {
                            //权限总是被拒绝
                            showPermissionDialog(context, *permission)
                        } else {
                            onCheckPermissionsListener.onError()
                        }
                    }
                    .start()
        } else {
            onCheckPermissionsListener.onSuccess()
        }
    }

    /**
     * 提示再次权限被拒绝
     */
    fun showPermissionDialog(context: Context, vararg permission: String) {
        if (null == alertDialog) {
            val builder = AlertDialog.Builder(context)
                    .setTitle(R.string.check_permission_reject)
                    .setMessage(R.string.check_permission_tip)
                    .setPositiveButton(R.string.setting) { dialog, which ->
                        goSetting(context, *permission)
                    }
                    .setNegativeButton(R.string.cancel) { dialog, which ->
                        onCheckPermissionsListener.onError()
                    }
            alertDialog = builder.create()
            alertDialog!!.setCancelable(false)
        }
        alertDialog!!.show()
    }

    /**
     * 去设置权限
     */
    fun goSetting(context: Context, vararg permission: String) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .onComeback {
                    //去设置权限回来，再判断
                    checkPermission(context, *permission)
                }.start()
    }

    /**
     * 权限申请回调
     */
    interface OnCheckPermissionsListener {
        fun onSuccess()

        fun onError()
    }
}