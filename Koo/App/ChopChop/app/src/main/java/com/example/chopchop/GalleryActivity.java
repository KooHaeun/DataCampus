package com.example.chopchop;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class GalleryActivity extends AppCompatActivity {

    private VideoView videoView; // 재생할 비디오뷰
    private String videoList[];  // 내부저장소내 비디오 리스트 
    private String videoDirPath;
    private String videoFilePath;
    @Override
    protected void onPause() {
        if(videoView!=null && videoView.isPlaying()) videoView.pause();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoView!=null)
            videoView.stopPlayback();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        videoDirPath = "/storage/self/primary/DCIM/젓가락질/";

        // 내부저장소에 미리 저장해둔 디렉토리의 경로를 가져온다.

        File file = new File(videoDirPath);
        videoList = file.list();
        // 파일 내 비디오 리스트를 가져온다. 

        String videoListReverse[] = new String[videoList.length];
        for(int i = 0; i< videoList.length; i++)
            videoListReverse[i] = videoList[videoList.length - i - 1];
        // 파일 내 저장된 비디오를 최신순으로 바꾸는 과정


        MyGalleryAdapter adapter = new MyGalleryAdapter( // 미리 만들어둔 adpater 선언
                getApplicationContext(), // 현재 화면의 제어권자
                R.layout.activity_row,
                videoListReverse);

        // adapterView
        Gallery gallery = (Gallery)findViewById(R.id.gallery1);
        gallery.setAdapter(adapter);


        final MediaController controller = new MediaController(this);
        // 비디오 mediacontroller

        // 갤러리에서 비디오 선택시 비디오 재생 
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) { // 선택되었을 때 콜백메서드
                videoFilePath = videoDirPath + videoListReverse[position];
                Log.e("VideoFilePath: ", videoFilePath);
                videoView =findViewById(R.id.videoVideo);
                videoView.setVideoPath(videoFilePath);
                videoView.requestFocus();
                videoView.setMediaController(controller);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        videoView.start();

                        // 첫화면이 보이게
                        // 그렇지 않으면 첫 화면은 그냥 검은 화면 
                        videoView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                controller.show(0);
                                videoView.pause();
                            }
                        },100);


                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

}

