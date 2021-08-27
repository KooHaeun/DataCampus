package com.example.chopchop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ResultActivity extends AppCompatActivity {

    Button finish;
    protected VideoView correct_chop = null, user_chop = null;
    TextView result_title;

    private String videoFilePath, old, newname, path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        init();
        Intent i = getIntent();
        if(i.getStringExtra("newVideo")==null) {
            old = i.getStringExtra("oldVideo");
            videoFilePath=old;
            newname = i.getStringExtra("filename");
            path = i.getStringExtra("path");
        }
        else
            videoFilePath=i.getStringExtra("newVideo");

        String right = i.getStringExtra("right");
        if(right.equals("notcorrect"))
            result_title.setText("올바르지 않은 젓가락질입니다");
        else
            result_title.setText("올바른 젓가락질입니다");

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(ResultActivity.this);
                dlg.setTitle("저장 여부");
                dlg.setMessage("오늘의 젓가락질을 저장하시겠습니까?");
                dlg.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            File file = new File(videoFilePath);
                            if (file.exists()) {
                                file.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                });

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(old!=null){
                            saveVideoToInternalStorage();
                        }

                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                });
                dlg.show();
            }

        });

        prepareVideoViewVideo();

    }
    protected void prepareVideoViewVideo() {
        // Set "sample.mp4" in raw directory
        Uri uri1 = Uri.parse("android.resource://" + getPackageName() + "/raw/example");
        correct_chop.setVideoURI(uri1);
        Uri uri2 = Uri.parse(videoFilePath);
        user_chop.setVideoURI(uri2);

        // Set the listener
        correct_chop.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                correct_chop.start();
            }
        });

        correct_chop.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                correct_chop.seekTo(0);
                correct_chop.start();
            }
        });
        user_chop.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                user_chop.start();
            }
        });

        user_chop.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                user_chop.seekTo(0);
                user_chop.start();
            }
        });
    }
    public void init(){
        Toolbar bar = findViewById(R.id.bar);
        setSupportActionBar(bar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back_button_64);
        finish = findViewById(R.id.finish);
        correct_chop = findViewById(R.id.correct_chop);
        user_chop = findViewById(R.id.user_chop);
        result_title = findViewById(R.id.result_title);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                AlertDialog.Builder dlg = new AlertDialog.Builder(ResultActivity.this);
                dlg.setTitle("알림");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setMessage("손 모양 인식을 다시 확인하시겠습니까?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResultActivity.super.onPause();
                        user_chop.pause();
                        correct_chop.pause();
                        Intent retry = new Intent(ResultActivity.this, CheckActivity.class);
                        retry.putExtra("new", videoFilePath);
                        startActivity(retry);
                        finish();
                    }
                });
                dlg.setNegativeButton("아니오", null);
                dlg.show();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveVideoToInternalStorage() {

        File newfile;


        try {

            File currentFile = new File(old);
            String fileName = newname;

            File wallpaperDirectory = new File(path);
            newfile = new File(wallpaperDirectory+"/", fileName + ".mp4");

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }


            if(currentFile.exists()){

                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Log.d("my", "Video file saved successfully.");

            }else{
                Log.d("my", "Video saving failed. Source file missing.");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
