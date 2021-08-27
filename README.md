# 데이터 청년 캠퍼스 고려대학교 과정 1조
## LSTM-AE를 활용한 젓가락질 교정 서비스

LSTM-AE를 활용한 젓가락질 교정 서비스는 영상 데이터를 바탕으로 젓가락질이 올바른지를 판단 및 지도합니다. 해당 서비스는 한국 젓가락 문화를 접하는 외국인, 젓가락질을 배우는 어린아이, 그리고 젓가락질을 잘 못 하는 어른을 타겟으로 합니다.

### 데이터 수집 방법

<img src="https://user-images.githubusercontent.com/87642864/131116062-73ceb2dc-3dfd-4daf-b785-4686ca72a909.PNG"  width="600" height="300">

활용데이터의 경우, 저희 조는 영상 촬영 방법을 정하여 젓가락질 영상 데이터를 직접 수집하였습니다. 


### 아이디어 구현방법

<img src="https://user-images.githubusercontent.com/87642864/131116436-5a28a112-7c34-49db-9bb7-142fd6d52b5b.PNG"  width="700" height="200">

#### 손가락 관절 좌표값 추출

<img src="https://user-images.githubusercontent.com/87642864/131116539-49ccc4f0-955a-400f-a3d3-634d16bae12e.PNG"  width="700" height="300">

Mediapipeline을 사용하여 16개의 손가락 관절 좌표값을 추출하였습니다.


### 데이터 전처리

##### 올바른 젓가락질의 특징

<img src="https://user-images.githubusercontent.com/87642864/131116974-749692cd-9b33-4c2c-80e0-e0cdf226edd4.PNG"  width="550" height="250">


##### Feature Engineering

<img src="https://user-images.githubusercontent.com/87642864/131117158-a329810a-4358-4f29-9098-6859c0782e8a.PNG"  width="500" height="200">

<img src="https://user-images.githubusercontent.com/87642864/131117200-d61319df-0a28-450f-9a5d-026fb4264e2d.PNG"  width="500" height="200">

손가락 마디를 연결하여 벡터값을 만든 후 Feature Engineering을 진행했습니다.  
추가한 Feature는 D_ratio, Sin(각도 차), 엄지-중지 내적, 검지-중지 내적 값입니다.

### 모델

<img src="https://user-images.githubusercontent.com/87642864/131117518-a062f481-510e-44ba-9e64-62a9867ed3c1.PNG"  width="750" height="200">

##### LSTM-AE를 선택한 이유

젓가락질의 영상을 시시각각 판단하기 위해 사진을 프레임별로 나열하여 움직이는 모습으로 옳고 그름을 판단하기 위하여 LSTM이 필요하였고, 젓가락질이 ‘정확한‘ 경우는 일정한 기준이 있는 반면 ‘정확하지 않은’ 경우는 매우 다양하기 때문에 ‘정확한‘ 젓가락질 모델을 학습시키고자 AutoEncoder를 적용하게 되었습니다.


### Application

<img src="https://user-images.githubusercontent.com/87642864/131119944-e4ecde7c-e346-4085-9006-e0512058d57d.PNG"  width="670" height="300">

결과물로 앱을 만들었으며, 영상촬영 또는 동영상 업로드를 통해 자신의 젓가락질이 옳고 그른지 확인할 수 있습니다.  
또한, 결과 화면에 나오는 올바른 젓가락질을 따라하며 연습할 수 있고 방금 분석한 젓가락질을 저장하여 추후 기록 확인 부분에서 찾아볼 수 있습니다.
