package com.example.chopchop;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {//완료

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    Button correct, album;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVerify();
        makeDir();
        correct = findViewById(R.id.correct);
        album = findViewById(R.id.album);

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CorrectActivity.class);
                i.putExtra("path", path);
                startActivity(i);
                finish();
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GalleryActivity.class);
                i.putExtra("path", path);
                startActivity(i);
                finish();
            }
        });
    }
    public void checkVerify() {
        TedPermission.with(this)
                .setPermissionListener(permission)
                .setRationaleMessage("앱을 사용하기 위해 권한을 허용해주세요.")
                .setDeniedMessage("권한이 거부되었습니다. 설정 > 권한에서 허용해주세요.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        }
    PermissionListener permission = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
        }
    };

    public void makeDir() {
        String root = "/storage/self/primary/DCIM/"; //내장에 만든다

        String directoryName = "chopchop";
        final File dir = new File(root + directoryName);
        if (!dir.exists()) {
            boolean wasSuccessful = dir.mkdir();
            if(wasSuccessful){
                path = root + directoryName;
            }else{
                path = root;
            }
        }else
            path = root + directoryName;
    }
    /*
    public void getModel(){
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("your_model", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        // Download complete. Depending on your app, you could enable the ML
                        // feature, or switch from the local model to the remote model, etc.

                        // The CustomModel object contains the local path of the model file,
                        // which you can use to instantiate a TensorFlow Lite interpreter.
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        }
                    }
                });
    }*/
}