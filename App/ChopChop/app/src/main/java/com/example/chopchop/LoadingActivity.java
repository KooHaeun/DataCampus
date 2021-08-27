package com.example.chopchop;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class LoadingActivity extends AppCompatActivity {

    private String videoFilePath, old, newname, path;
    public static Integer cnt=0;
    Bitmap bmp[];
    public static ArrayList<ArrayList<Float>> datas = new ArrayList<ArrayList<Float>>();
    public static ArrayList<Float> data = new ArrayList<Float>();
    public static Float preproData[][][];
    Interpreter tflite;
    public static boolean start=false;

    private Hands hands;
    private HandsResultImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        init();






    }


    public void init(){
        Intent in = getIntent();
        if(in.getStringExtra("newVideo")==null) {
            old = in.getStringExtra("oldVideo");
            videoFilePath=old;
            newname = in.getStringExtra("filename");
            path = in.getStringExtra("path");
        }
        else
            videoFilePath=in.getStringExtra("newVideo");
        bmp = new Bitmap[32];


        Thread thread1 = new Thread() {
            public void run() {
                FFmpegMediaMetadataRetriever med = new FFmpegMediaMetadataRetriever();
                med.setDataSource(videoFilePath);
                for(int i=0; i<32; i++){
                    bmp[i] = med.getFrameAtTime(i * 100000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
                    Log.d("bmp", String.valueOf(bmp[i]) + String.valueOf(i));
                    setupStaticImageDemoUiComponents(bmp[i]);
                }



            }
        };

        thread1.start();



    }

    private void setupStaticImageDemoUiComponents(Bitmap bm) {
        // The Intent to access gallery and read images as bitmap.

            setupStaticImageModePipeline();
            Bitmap bitmap = bm;
            Log.d("TAG", String.valueOf(bitmap));
            if (bitmap != null) {
                hands.send(bitmap);
            }

            imageView = new HandsResultImageView(this);

        }


    // The core MediaPipe Hands setup workflow for its static image mode.
    private void setupStaticImageModePipeline() {
        // Initializes a new MediaPipe Hands instance in the static image mode.
        hands =
                new Hands(
                        this,
                        HandsOptions.builder()
                                .setMode(HandsOptions.STATIC_IMAGE_MODE)
                                .setMaxNumHands(1)
                                .build());

        // Connects MediaPipe Hands to the user-defined HandsResultImageView.
        hands.setResultListener(
                handsResult -> {
                    logWristLandmark(handsResult, /*showPixelValues=*/ true);
                    imageView.setHandsResult(handsResult);
                    if(start){
                        Log.d("model", "start");

                        tflite = getTfliteInterpreter("LSTM_AE.tflite");
                        Log.d("model", "start1");
                        float output[][][]=new float[1][32][37];
                        Log.d("model", "start2");
                        tflite.run(preproData, output);
                        Log.d("model", "start3");

                        Log.d("model", String.valueOf(output[0][0][0]));

                    }

                });



    }

    private void logWristLandmark(HandsResult result, boolean showPixelValues) {
        LandmarkProto.NormalizedLandmark wristLandmark = Hands.getHandLandmark(result, 0, HandLandmark.WRIST);
        // For Bitmaps, show the pixel values. For texture inputs, show the normalized coordinates.
        if (showPixelValues) {
            int width = result.inputBitmap().getWidth();
            int height = result.inputBitmap().getHeight();
            Log.i(
                    "TAG",
                    String.format(
                            "MediaPipe Hand wrist coordinates (pixel values): x=%f, y=%f",
                            wristLandmark.getX() * width, wristLandmark.getY() * height));
        } else {
            Log.i(
                    "TAG",
                    String.format(
                            "MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
                            wristLandmark.getX(), wristLandmark.getY()));
        }
    }
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(LoadingActivity.this, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


}
