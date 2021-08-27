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


#### 데이터 전처리

##### Feature 직접 만들기

<img src="https://user-images.githubusercontent.com/87642864/131116974-749692cd-9b33-4c2c-80e0-e0cdf226edd4.PNG"  width="550" height="250">


##### Feature에 맞춰서 전처리

<img src="https://user-images.githubusercontent.com/87642864/131117158-a329810a-4358-4f29-9098-6859c0782e8a.PNG"  width="600" height="270">

<img src="https://user-images.githubusercontent.com/87642864/131117200-d61319df-0a28-450f-9a5d-026fb4264e2d.PNG"  width="600" height="270">


#### 모델

<img src="https://user-images.githubusercontent.com/87642864/131117518-a062f481-510e-44ba-9e64-62a9867ed3c1.PNG"  width="750" height="200">

##### LSTM-AE를 선택한 이유

젓가락질의 영상을 시시각각 판단하기 위해 사진을 프레임별로 나열하여 움직이는 모습으로 옳고 그름을 판단하기 위하여 LSTM이 필요하였고,
젓가락질이 ‘정확한‘ 경우는 일정한 기준이 있는 반면 ‘정확하지 않은’ 경우는 매우 다양하기 때문에 ‘정확한‘ 젓가락질 모델을 학습시키고자
AutoEncoder를 적용하게 되었습니다.


##### 시계열 시각화

<img src="https://user-images.githubusercontent.com/87642864/131117964-38e57bef-484b-4437-9084-50c0b8afb044.PNG"  width="750" height="400">


##### 모델링

<img src="https://user-images.githubusercontent.com/87642864/131118675-ed07caa0-884f-4237-9e6f-ddc04977f762.PNG"  width="600" height="300">

<img src="https://user-images.githubusercontent.com/87642864/131118729-ced5dc11-e33d-4881-ae45-af93ced5fb0d.PNG"  width="600" height="300">
