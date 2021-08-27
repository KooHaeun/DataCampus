package com.example.chopchop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutioncore.VideoInput;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

public class CheckActivity extends AppCompatActivity {
    private static final String TAG = "CheckActivity";
    private Hands hands;
    private enum InputSource {
        UNKNOWN,
        VIDEO
    }
    private VideoInput videoInput;
    private SolutionGlSurfaceView<HandsResult> glSurfaceView;
    private CheckActivity.InputSource inputSource = CheckActivity.InputSource.UNKNOWN;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        init();
        Intent i = getIntent();
        String newVideo = i.getStringExtra("new");
        Uri uri = Uri.parse(newVideo);
        stopCurrentPipeline();
        setupStreamingModePipeline(CheckActivity.InputSource.VIDEO);
        glSurfaceView.post(
                () -> videoInput.start(
                        this,
                        uri,
                        hands.getGlContext(),
                        glSurfaceView.getWidth(),
                        glSurfaceView.getHeight()

                ));

        Button next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(CheckActivity.this);
                dlg.setTitle("확인");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setMessage("손 모양이 제대로 인식되나요?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPause();
                        Intent result = new Intent(getApplicationContext(), LoadingActivity.class);
                        result.putExtra("newVideo", newVideo);
                        startActivity(result);
                        finish();
                    }
                });
                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPause();
                        Intent retry = new Intent(getApplicationContext(), CorrectActivity.class);
                        retry.putExtra("error", 1);
                        startActivity(retry);
                        finish();
                    }
                });
                dlg.show();

            }
        });

        }
    protected void onResume() {
        super.onResume();
        if (inputSource == CheckActivity.InputSource.VIDEO) {
            videoInput.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (inputSource == CheckActivity.InputSource.VIDEO) {
            videoInput.pause();
        }
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
        FrameLayout frameLayout = findViewById(R.id.preview_display_layout);

        frameLayout.removeAllViewsInLayout();
        frameLayout.addView(glSurfaceView);
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
    public void init(){
        Toolbar bar = findViewById(R.id.bar);
        setSupportActionBar(bar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back_button_64);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                AlertDialog.Builder dlg = new AlertDialog.Builder(CheckActivity.this);
                dlg.setTitle("확인");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setMessage("손 모양이 제대로 인식되지 않나요?");
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPause();
                        Intent retry = new Intent(CheckActivity.this, CorrectActivity.class);
                        retry.putExtra("error", 1);
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
}
