package com.example.chopchop;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryAdapter extends BaseAdapter {//완료
        Context context;
        int layout;
        String [] videoTitle;  // 비디오 제목
        LayoutInflater inf;
        String filePath;

        public GalleryAdapter(Context context, int layout, String[] videoTitle, String path) {

            this.context = context;
            this.layout = layout;
            this.videoTitle = videoTitle;
            inf = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            filePath = path;

        }

        @Override
        public int getCount() { // 보여줄 데이터의 총 개수 - 필수
            return videoTitle.length;
        }

        @Override
        public Object getItem(int position) { // 해당행의 데이터- 선택

            return null;
        }

        @Override
        public long getItemId(int position) { // 해당행의 유니크한 id-선택
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 보여줄 해당행의 row xml 파일의 데이터를 셋팅해서 뷰를 완성하는 작업

            if (convertView == null) {
                convertView = inf.inflate(layout, null);
            }
            // 비디오 썸네일 만들기
            Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath+"/" + videoTitle[position],
                    MediaStore.Images.Thumbnails.MINI_KIND); // MINI_KIND크기

            if(bmThumbnail !=null) {
                // 썸네일 크기 조절
                bmThumbnail = Bitmap.createBitmap(bmThumbnail, 0, bmThumbnail.getHeight() / 5, bmThumbnail.getWidth(), bmThumbnail.getHeight() * 3 / 5);
            }

            ImageView imageThumbnail = (ImageView)convertView.findViewById(R.id.thumbnail);
            imageThumbnail.setImageBitmap(bmThumbnail);

            // 비디오 제목 정해주기
            TextView textView = convertView.findViewById(R.id.video_title);
            if(videoTitle[position] != null) {
                textView.setText(videoTitle[position]);
            }else{
                textView.setText("잘못된 형식");
            }


            return convertView;
        }
}
