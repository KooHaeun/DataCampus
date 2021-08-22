package com.example.chopchop;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ResultActivity extends AppCompatActivity {

    Button finish;
    protected VideoView user_chop =null;
    protected TextureView correct_chop=null;
    protected MediaPlayer mPlayer = null;

    private String videoFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        Intent i = getIntent();
        finish = findViewById(R.id.finish);

        videoFilePath = i.getStringExtra("newVideo");

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), videoFilePath, Toast.LENGTH_LONG).show();
                AlertDialog.Builder dlg = new AlertDialog.Builder(ResultActivity.this);
                dlg.setTitle("저장 여부");
                dlg.setMessage("오늘의 젓가락질을 저장하시겠습니까?");
                dlg.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            File file = new File(videoFilePath);
                            if(file.exists()){
                                file.delete();
                            }
                        }catch (Exception e){
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
                                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(main);
                                finish();
                            }
                        });
                        dlg.show();
            }

        });

        // Get controls
        getControls();

        // Prepare video for TextureView
        prepareTextureViewVideo();

        // Prepare video for VideoView
        prepareVideoViewVideo();
    }
    protected void getControls() {
        // Get buttons
        Button btn = (Button) findViewById(R.id.btnPlayTV);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTextureView();
            }
        });
        btn = (Button) findViewById(R.id.btnPlayVV);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideoView();
            }
        });
        btn = (Button) findViewById(R.id.btnStopTV);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTextureView();
            }
        });
        btn = (Button) findViewById(R.id.btnStopVV);
        btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVideoView();
            }
        });

        // Get views
        correct_chop = (TextureView) findViewById(R.id.correct_chop);
        user_chop = (VideoView) findViewById(R.id.user_chop);
    }
    protected class MyTexureViewListener implements TextureView.SurfaceTextureListener {
        Context mContext;
        String mVideoSource;

        public MyTexureViewListener(Context context, String source) {
            mContext = context;
            mVideoSource = source;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            try {
                // Create MediaPlayer
                mPlayer = new MediaPlayer();

                // Set the surface
                Surface surface = new Surface(surfaceTexture);
                mPlayer.setSurface(surface);

                // Set the video source
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/example");
                mPlayer.setDataSource(mContext, uri);

                // Prepare: In case of local file prepare() can be used, but for streaming, prepareAsync() is a must
                mPlayer.prepareAsync();

                // Wait for the preparation
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // Play the video
                        playTextureView();
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    }

    protected void prepareTextureViewVideo() {
        // Set the listener to play "sample.mp4" in raw directory
        correct_chop.setSurfaceTextureListener(new MyTexureViewListener(this, "sample"));
    }

    protected void playTextureView() {
        // Play it
        mPlayer.start();
    }

    protected void stopTextureView() {
        // Pause it. If stopped, mPlayer should be prepared again.
        mPlayer.pause();
    }

    /////////////////////////////////////////////////////////////////////////////////
    // VideoView related code from here
    /////////////////////////////////////////////////////////////////////////////////

    protected void prepareVideoViewVideo() {
        // Set "sample.mp4" in raw directory
        Uri uri = Uri.parse(videoFilePath);
        user_chop.setVideoURI(uri);

        // Set the listener
        user_chop.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playVideoView();
            }
        });
    }

    protected void playVideoView() {
        user_chop.start();
    }

    protected void stopVideoView() {
        user_chop.pause();
    }
    public Uri getUriFromPath(String filePath) {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, videoFilePath, null, null);

        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        return uri;
    }
}
