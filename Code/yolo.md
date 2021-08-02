0. R-CNN는 정확도는 높으나 속도가 느리다.(classification)
   -> YOLO : regression, CNN 방식 사용
참고링크 : https://deepbaksuvision.github.io/Modu_ObjectDetection/posts/04_01_Review_of_YOLO_Paper.html


1. YOLOX : Exceeding YOLO Series in 2021
참고링크 : https://github.com/Megvii-BaseDetection/YOLOX/blob/main/docs/train_custom_data.md#train-custom-data

2. YOLOv3 : 가장 많이 상용화된 YOLO
라벨링: YOLO-mark -> https://github.com/AlexeyAB/Yolo_mark

----------------------------------------------------------------------------------------------------------------
<YOLOX - 사용자 데이터 학습>
1.create dataset
 1) 라벨링을 한다 : Labelme 또는 CVAT 활용
참고링크 : (Labelme) https://github.com/wkentaro/labelme 

 2) 대응하는 Dataset Class : __getitem__
     -> COCO format
     -> VOC format
 3) evaluator metric : COCO evaluator and VOC evaluator

2. create your exp file
3. Train

<YOLOv3>
opencv 다운로드 필수