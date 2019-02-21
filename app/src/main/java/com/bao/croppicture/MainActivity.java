package com.bao.croppicture;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PermissionUtils permissionUtils;
    private CropImageUtils cropImageUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_photo).setOnClickListener(this);
        findViewById(R.id.btn_album).setOnClickListener(this);

        //初始化裁剪工具
        cropImageUtils = new CropImageUtils(this);


        //申请权限
        permissionUtils = new PermissionUtils(new PermissionUtils.OnCheckPermissionsListener() {
            @Override
            public void onSuccess() {
                ToastUtils.INSTANCE.longToast("权限申请成功！");
            }

            @Override
            public void onError() {
                ToastUtils.INSTANCE.longToast("权限申请失败！");
            }
        });
        permissionUtils.checkPermission(this, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //拍照
            case R.id.btn_photo:
                cropImageUtils.takePhoto();
                break;
            //打开相册
            case R.id.btn_album:
                cropImageUtils.openAlbum();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cropImageUtils.onActivityResult(requestCode, resultCode, data, new CropImageUtils.OnResultListener() {
            @Override
            public void takePhotoFinish(String path) {
                ToastUtils.INSTANCE.longToast("照片存放在：" + path);
                //拍照回调，去裁剪
                cropImageUtils.cropPicture(path);
            }

            @Override
            public void selectPictureFinish(String path) {
                ToastUtils.INSTANCE.longToast("打开图片：" + path);
                //相册回调，去裁剪
                cropImageUtils.cropPicture(path);
            }

            @Override
            public void cropPictureFinish(String path) {
                ToastUtils.INSTANCE.longToast("裁剪保存在：" + path);
                //裁剪回调
                Glide.with(MainActivity.this)
                        .load(path)
                        .into((ImageView) findViewById(R.id.image_result));
            }
        });
    }
}
