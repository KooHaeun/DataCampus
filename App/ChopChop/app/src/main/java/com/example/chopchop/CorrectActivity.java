package com.example.chopchop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutioncore.VideoInput;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CorrectActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CorrectActivity";
    private Hands hands;

    private enum InputSource {
        UNKNOWN,
        VIDEO,
        CAMERA
    }

    private FrameLayout frameLayout;
    private VideoInput videoInput;
    private ActivityResultLauncher<Intent> videoGetter;
    private CameraInput cameraInput;
    private SolutionGlSurfaceView<HandsResult> glSurfaceView;
    private CorrectActivity.InputSource inputSource = CorrectActivity.InputSource.UNKNOWN;

    private TextView warning, cor_text;
    private ImageButton cap, vid;
    private SurfaceView surview;
    private Camera camera;
    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private boolean recording = false;
    private Button next;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh시 mm분 ss초");
    String newVideo = null, oldVideo=null;
    String path, filename;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct);


        init();
        Intent error = getIntent();
        Integer msg = error.getIntExtra("error", 0);
        if (msg == 1) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(CorrectActivity.this);
            dlg.setTitle("알림");
            dlg.setIcon(R.mipmap.ic_launcher);
            dlg.setMessage("주의사항과 예시에 맞는 새로운 젓가락질 영상을 올리거나 촬영해주세요");
            dlg.setPositiveButton("확인", null);
            dlg.show();
        }


        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!recording) {
                    surview.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CorrectActivity.this, "촬영 시작", Toast.LENGTH_SHORT).show();
                            String filename = getTime();
                            try {
                                recorder = new MediaRecorder();
                                camera.unlock();

                                recorder.setCamera(camera);

                                recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                                recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
                                recorder.setOutputFile(path + "/" + filename + ".mp4");
                                recorder.setPreviewDisplay(holder.getSurface());
                                recorder.prepare();
                                recorder.start();
                                recording = true;
                                newVideo = path + "/" + filename + ".mp4";
                            } catch (Exception e) {
                                Toast.makeText(CorrectActivity.this, "에러", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                recorder.release();
                            }
                        }
                    });

                } else if (recording) {
                    recorder.stop();
                    recorder.release();
                    recording = false;
                    Intent check =
                            new Intent(getApplicationContext(), CheckActivity.class);
                    check.putExtra("new", newVideo);
                    startActivity(check);
                    finish();

                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
                AlertDialog.Builder dlg = new AlertDialog.Builder(CorrectActivity.this);
                dlg.setTitle("확인");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setMessage("손 모양이 제대로 인식되나요?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(CorrectActivity.this, LoadingActivity.class);
                        i.putExtra("oldVideo", oldVideo);
                        i.putExtra("filename", filename);
                        i.putExtra("path", path);
                        startActivity(i);
                        finish();
                    }
                });
                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPause();
                        stopCurrentPipeline();
                        vid.setVisibility(View.VISIBLE);
                        cap.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                        frameLayout.removeAllViewsInLayout();
                        frameLayout.addView(cor_text);
                        frameLayout.addView(surview);
                        frameLayout.requestLayout();
                    }
                });
                dlg.show();


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
                ex.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.seekTo(0);
                        mp.start();
                    }
                });
                dlg.setTitle("예시 및 주의사항");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setView(show);
                dlg.setPositiveButton("확인", null);
                dlg.show();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (inputSource == InputSource.VIDEO) {
            videoInput.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (inputSource == InputSource.VIDEO) {
            videoInput.pause();
        }
    }

    private void setupVideoDemoUiComponents() {
        // 여기서 영상 촬영 뒤에 불러오는 것도 구현
        videoGetter =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            Intent resultIntent = result.getData();
                            if (resultIntent != null) {
                                if (result.getResultCode() == RESULT_OK) {
                                    glSurfaceView.post(
                                            () -> videoInput.start(
                                                    this,
                                                    resultIntent.getData(),
                                                    hands.getGlContext(),
                                                    glSurfaceView.getWidth(),
                                                    glSurfaceView.getHeight()));
                                    oldVideo = getPath(resultIntent.getData());
                                    filename = getTime();


                                }
                            }

                        });

        vid.setOnClickListener(
                v -> {
                    vid.setVisibility(View.GONE);
                    cap.setVisibility(View.GONE);
                    stopCurrentPipeline();
                    setupStreamingModePipeline(CorrectActivity.InputSource.VIDEO);
                    // Reads video from gallery.
                    Intent gallery =
                            new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                    videoGetter.launch(gallery);

                    next.setVisibility(View.VISIBLE);
                    next.setEnabled(true);
                });

    }



    private void setupStreamingModePipeline(InputSource inputSource) {
        this.inputSource = inputSource;
        // Initializes a new MediaPipe Hands instance in the streaming mode.
        hands =
                new Hands(
                        this,
                        HandsOptions.builder()
                                .setMode(HandsOptions.STREAMING_MODE)
                                .setMaxNumHands(1)
                                .build());
        hands.setErrorListener((message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));

        if (inputSource == InputSource.VIDEO) {
            // Initializes a new VideoInput instance and connects it to MediaPipe Hands.
            videoInput = new VideoInput(this);
            videoInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
        }else if(inputSource == InputSource.CAMERA){
            videoInput = new VideoInput(this);
            videoInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
        }

        // Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
        glSurfaceView =
                new SolutionGlSurfaceView<>(this, hands.getGlContext(), hands.getGlMajorVersion());
        glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer());
        glSurfaceView.setRenderInputImage(true);
        hands.setResultListener(
                handsResult -> {
                    logWristLandmark(handsResult, /*showPixelValues=*/ false);
                    glSurfaceView.setRenderData(handsResult);
                    glSurfaceView.requestRender();
                });

        // The runnable to start camera after the gl surface view is attached.
        // For video input source, videoInput.start() will be called when the video uri is available.


        // Updates the preview layout.
        frameLayout = findViewById(R.id.preview_display_layout);
        frameLayout.removeAllViewsInLayout();
        frameLayout.addView(glSurfaceView);
        frameLayout.setRotation(90);
        glSurfaceView.setVisibility(View.VISIBLE);
        frameLayout.requestLayout();
    }
    private void stopCurrentPipeline() {
        if (videoInput != null) {
            videoInput.setNewFrameListener(null);
            videoInput.close();
        }
        if (glSurfaceView != null) {
            glSurfaceView.setVisibility(View.GONE);
        }
        if (hands != null) {
            hands.close();
        }
    }

    private void logWristLandmark(HandsResult result, boolean showPixelValues) {
        LandmarkProto.NormalizedLandmark wristLandmark = Hands.getHandLandmark(result, 0, HandLandmark.WRIST);
        // For Bitmaps, show the pixel values. For texture inputs, show the normalized coordinates.
        if (showPixelValues) {
            int width = result.inputBitmap().getWidth();
            int height = result.inputBitmap().getHeight();
            Log.i(
                    TAG,
                    String.format(
                            "MediaPipe Hand wrist coordinates (pixel values): x=%f, y=%f",
                            wristLandmark.getX() * width, wristLandmark.getY() * height));
        } else {
            Log.i(
                    TAG,
                    String.format(
                            "MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
                            wristLandmark.getX(), wristLandmark.getY()));
        }
    }
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

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
    public void init(){


        warning = findViewById(R.id.warning);
        surview = findViewById(R.id.camera_preview);
        cap = findViewById(R.id.cap);
        vid = findViewById(R.id.video);
        next = findViewById(R.id.next);
        cor_text = findViewById(R.id.cor_text);
        frameLayout = findViewById(R.id.preview_display_layout);
        setupVideoDemoUiComponents();

        Toolbar bar = findViewById(R.id.bar);
        setSupportActionBar(bar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back_button_64);

        Intent i = getIntent();
        path = i.getStringExtra("path");

        camera = Camera.open();
        camera.setDisplayOrientation(90);
        holder = surview.getHolder();
        holder.addCallback(CorrectActivity.this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                Intent main = new Intent(CorrectActivity.this, MainActivity.class);
                startActivity(main);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }




}