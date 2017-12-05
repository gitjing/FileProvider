package com.example.ljj.fileproviderdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 兼容7.0以上手机系统的共享文件
 */
public class MainActivity extends AppCompatActivity {

    private static final String providerAuthority = "com.ljj.fileproviderdemo.fileprovider";
    private static final String dirName = "/test";
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    takePhotoNoCompress();
                }
            }
        });
    }


    private String mCurrentPhotoPath;

    public void takePhotoNoCompress() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                    .format(new Date()) + ".png";

            File directoryPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dirName);

            if (!directoryPath.exists()) {
                if (!directoryPath.mkdirs()) {
                    directoryPath.mkdirs();
                }
            }

            mCurrentPhotoPath = directoryPath.getAbsolutePath() + "/" + filename;

            Uri fileUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                fileUri = FileProvider.getUriForFile(this, providerAuthority, new File(mCurrentPhotoPath));
            } else {
                fileUri = Uri.fromFile(new File(mCurrentPhotoPath));
            }

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // 对于面向 Android 7.0 的应用，Android 框架执行的 StrictMode API 政策禁止在您的应用外部公开 file:// URI。
            // 如果一项包含文件 URI 的 intent 离开您的应用，则应用出现故障，并出现 FileUriExposedException 异常。
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }
        // else tip?

    }

}
