// Copyright 2021 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.chopchop;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsResult;

import org.tensorflow.lite.Interpreter;

import java.util.ArrayList;
import java.util.List;

/** An ImageView implementation for displaying MediaPipe Hands results. */
public class HandsResultImageView extends AppCompatImageView {
  private static final String TAG = "HandsResultImageView";

  private Bitmap latest;
  int cnt=0;
  Float d_ratio[] = new Float[32];
  Float d_ratio_sq[] = new Float[32];
  Float df_vector[][] = new Float[32][32];
  Float df_size[][] = new Float[32][16];
  Float df_unitvector[][] = new Float[32][32];
  ArrayList<sind_difData> sin_df = new ArrayList<sind_difData>();
  ArrayList<dotData> dotProduct = new ArrayList<dotData>();
  Float df_vector_final[][] =new Float[32][32];
  boolean myreturn=false;

  


  public HandsResultImageView(Context context) {
    super(context);
    setScaleType(AppCompatImageView.ScaleType.FIT_CENTER);
  }

  /**
   * Sets a {@link HandsResult} to render.
   *
   * @param result a {@link HandsResult} object that contains the solution outputs and the input
   *               {@link Bitmap}.
   */
  public void setHandsResult(HandsResult result) {

    Bitmap bmInput = result.inputBitmap();
    int width = bmInput.getWidth();
    int height = bmInput.getHeight();
    latest = Bitmap.createBitmap(width, height, bmInput.getConfig());
    Canvas canvas = new Canvas(latest);

    canvas.drawBitmap(bmInput, new Matrix(), null);
      drawLandmarksOnCanvas(
              result.multiHandLandmarks().get(0).getLandmarkList(), canvas, width, height);

    LoadingActivity.datas.add(LoadingActivity.data);
    if(LoadingActivity.cnt==32) {
      preprocess();
      LoadingActivity.start =true;
    }
  }

  // TODO: Better hand landmark and hand connection drawing.
  private void drawLandmarksOnCanvas(
          List<NormalizedLandmark> handLandmarkList, Canvas canvas, int width, int height) {


    // Draw connections.
    for (Hands.Connection c : Hands.HAND_CONNECTIONS) {
      Paint connectionPaint = new Paint();
      NormalizedLandmark start = handLandmarkList.get(c.start());
      NormalizedLandmark end = handLandmarkList.get(c.end());
      canvas.drawLine(
              start.getX() * width,
              start.getY() * height,
              end.getX() * width,
              end.getY() * height,
              connectionPaint);

      LoadingActivity.data.clear();
    }
    // Draw landmarks.
    int i=0;
    for (LandmarkProto.NormalizedLandmark landmark : handLandmarkList) {
      Log.d("my"+String.valueOf(i), "x: " + String.valueOf(landmark.getX()) + " y:" + String.valueOf(landmark.getY()));
      if(i<=16) {
        LoadingActivity.data.add(landmark.getX());
        LoadingActivity.data.add(landmark.getY());
      }

      i++;
    }
    Log.d("my"+String.valueOf(i), String.valueOf(LoadingActivity.data.size()));

    LoadingActivity.cnt++;

    //손 좌표 가져오기
  }
  private void preprocess(){
    Log.d("preprocess0", "0");
    for(int num=0; num<32; num++) {

      d_ratio[num]= Float.parseFloat(String.valueOf((Math.sqrt(Math.pow((LoadingActivity.datas.get(num).get(24)-LoadingActivity.datas.get(num).get(32)),2)+Math.pow((LoadingActivity.datas.get(num).get(25)-LoadingActivity.datas.get(num).get(33)),2)))/(Math.sqrt(Math.pow((LoadingActivity.datas.get(num).get(16)-LoadingActivity.datas.get(num).get(24)),2)+Math.pow((LoadingActivity.datas.get(num).get(17)-LoadingActivity.datas.get(num).get(25)),2)))));
      d_ratio_sq[num] = Float.parseFloat(String.valueOf((Math.pow((LoadingActivity.datas.get(num).get(24)-LoadingActivity.datas.get(num).get(32)),2)+Math.pow((LoadingActivity.datas.get(num).get(25)-LoadingActivity.datas.get(num).get(33)),2))/(Math.pow((LoadingActivity.datas.get(num).get(16)-LoadingActivity.datas.get(num).get(24)),2)+Math.pow((LoadingActivity.datas.get(num).get(17)-LoadingActivity.datas.get(num).get(25)),2))));
      Log.d("d_ratio_sq[num]", String.valueOf(d_ratio[num])+"/"+String.valueOf(d_ratio_sq[num]));
    }
    for(int num=0; num<32; num++) {

      for(int i=0;i<32;i++){
        df_vector[num][i]= LoadingActivity.datas.get(num).get(i+2)-LoadingActivity.datas.get(num).get(i);
        Log.d("df_vector[num][i]", String.valueOf(df_vector[num][i]));
      }
    }
    for(int num=0; num<32; num++) {

      for(int i=0;i<31;i+=2){
        df_size[num][i/2]=Float.parseFloat(String.valueOf((Math.sqrt(Math.pow(df_vector[num][i],2)+Math.pow(df_vector[num][i+1],2)))));
        Log.d("df_size", String.valueOf(df_size[num][i/2]));
      }
    }
    for(int num=0; num<32; num++) {
      for(int i=0;i<32;i++){
        df_unitvector[num][i]=(df_vector[num][i]/df_size[num][i/2]);
        Log.d("df_unitvector", String.valueOf(df_unitvector[num][i]));
      }
    }
    for(int num=0; num<32; num++) {
      sin_df.add(new sind_difData((df_unitvector[num][12]*df_unitvector[num][20]+df_unitvector[num][13]*df_unitvector[num][21]),
              (df_unitvector[num][28]*df_unitvector[num][20]+df_unitvector[num][21]*df_unitvector[num][29]),
              (df_unitvector[num][13]*df_unitvector[num][20]-df_unitvector[num][12]*df_unitvector[num][21]),
              (df_unitvector[num][21]*df_unitvector[num][28]-df_unitvector[num][20]*df_unitvector[num][29]),
              (((df_unitvector[num][13]*df_unitvector[num][20])-(df_unitvector[num][12]*df_unitvector[num][21]))
                      *((df_unitvector[num][28]*df_unitvector[num][20])+(df_unitvector[num][21]*df_unitvector[num][29]))
                      -(((df_unitvector[num][21]*df_unitvector[num][28])-(df_unitvector[num][20]*df_unitvector[num][29]))
                      *(((df_unitvector[num][12]*df_unitvector[num][20])+(df_unitvector[num][13]*df_unitvector[num][21])))))));
      Log.d("sin_df", String.valueOf(sin_df.get(num).getSin_dif()));

    }
    for(int num=0; num<32; num++){
      dotProduct.add(new dotData(((df_unitvector[num][6]*df_unitvector[num][22])+(df_unitvector[num][7]*df_unitvector[num][23])),
              ((df_unitvector[num][14]*df_unitvector[num][22])+(df_unitvector[num][15]*df_unitvector[num][23]))));
      Log.d("dotProduct", String.valueOf(dotProduct.get(num).getUm()));
    }
    for(int num=0;num<32;num++){
      for(int i =0; i<32;i++) {
        df_vector_final[num][i] = ((df_vector[num][i] / df_size[num][15]));
        Log.d("df_vector_final", String.valueOf(df_vector_final[num][i]));
      }
    }


    LoadingActivity.preproData = new Float[1][32][37];
    for(int num=0; num<32; num++){
      for(int i=0;i<32; i++) {
        Log.d("preprocess", String.valueOf(num));
        LoadingActivity.preproData[0][num][i] = df_vector_final[num][i];

        Log.d("preproSize0", String.valueOf(LoadingActivity.preproData[0]) + "/" + String.valueOf(LoadingActivity.preproData[0][num]) + "/" + String.valueOf(LoadingActivity.preproData[0][num][i]));
      }

        LoadingActivity.preproData[0][num][32] = d_ratio[num];
        LoadingActivity.preproData[0][num][33] = d_ratio_sq[num];
        LoadingActivity.preproData[0][num][34] = sin_df.get(num).getSin_dif();
        LoadingActivity.preproData[0][num][35] = dotProduct.get(num).getUm();
        LoadingActivity.preproData[0][num][36] = dotProduct.get(num).getGum();

        Log.d("preproSize", String.valueOf(LoadingActivity.preproData[0]) + "/" + String.valueOf(LoadingActivity.preproData[0][num]) + "/" + String.valueOf(LoadingActivity.preproData[0][num][31]));
    }
  }


}
