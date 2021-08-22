package com.example.chopchop;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CorrectActivity extends AppCompatActivity implements SurfaceHolder.Callback
{

    private TextView warning;
    private ImageButton cap;
    private ImageView in;
    private SurfaceView surview;
    private Camera camera;
    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private boolean recording = false;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh.mm");
    String newVideo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct);

        warning = findViewById(R.id.warning);
        surview = findViewById(R.id.camera_preview);

        cap = findViewById(R.id.cap);
        in = findViewById(R.id.start_record);
        in.bringToFront();

        TedPermission.with(this)
                .setPermissionListener(permission)
                .setRationaleMessage("촬영을 위해 권한을 허용해주세요.")
                .setDeniedMessage("권한이 거부되었습니다. 설정 > 권한에서 허용해주세요.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();


        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(CorrectActivity.this);
                dlg.setTitle("파일 뭐로 할 지");
                dlg.setPositiveButton("파일 업로드", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                startActivityForResult(intent,10);
                            }
                        });
                dlg.setNegativeButton("영상 촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        v.setVisibility(View.INVISIBLE);
                        v.setEnabled(false);
                        surview.setVisibility(View.VISIBLE);
                        cap.setVisibility(View.VISIBLE);
                        if(v.getVisibility()==View.INVISIBLE&&surview.getVisibility()==View.VISIBLE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CorrectActivity.this, "촬영 시작", Toast.LENGTH_SHORT).show();
                                    String filename = getTime();
                                    try {
                                        File path = Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_DCIM);
                                        recorder = new MediaRecorder();
                                        camera.unlock();
                                        recorder.setCamera(camera);
                                        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                                        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
                                        recorder.setOrientationHint(90);
                                        recorder.setOutputFile(path+"/젓가락질/" + filename + ".mp4");
                                        recorder.setPreviewDisplay(holder.getSurface());
                                        recorder.prepare();
                                        recorder.start();
                                        recording = true;
                                        newVideo = "/storage/self/primary/DCIM/젓가락질/" + filename + ".mp4";
                                    } catch (Exception e) {
                                        Toast.makeText(CorrectActivity.this, "작", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                        recorder.release();
                                    }
                                }
                            });
                        }
                    }
                });
                dlg.show();


            }
        });
        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    recorder.stop();
                    recorder.release();
                    recording = false;

                }
                if(!recording) {
                    Intent i = new Intent(CorrectActivity.this, ResultActivity.class);
                    i.putExtra("newVideo", newVideo);
                    startActivity(i);
                    finish();
                }

            }
        });

        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View show = View.inflate(CorrectActivity.this, R.layout.warning_dialog, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(CorrectActivity.this);
                VideoView ex = show.findViewById(R.id.example);
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/example");
                ex.setVideoURI(uri);
                ex.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // 준비 완료되면 비디오 재생
                        mp.start();
                    }
                });
                dlg.setTitle("예시 및 주의사항");
                dlg.setView(show);
                dlg.setPositiveButton("확인", null);
                dlg.show();
            }
        });
    }
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
    PermissionListener permission = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(CorrectActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            holder = surview.getHolder();
            holder.addCallback(CorrectActivity.this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(CorrectActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    private void refreshCamera(Camera camera) {
        if(holder.getSurface()==null){
            return;
        }

        try{
            camera.stopPreview();
        }catch(Exception e){
            e.printStackTrace();
        }

        setCamera(camera);
    }

    private void setCamera(Camera cam) {
        camera = cam;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        refreshCamera(camera);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}
