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

<img src="https://user-images.githubusercontent.com/87642864/131117158-a329810a-4358-4f29-9098-6859c0782e8a.PNG"  width="650" height="250">

<img src="https://user-images.githubusercontent.com/87642864/131117200-d61319df-0a28-450f-9a5d-026fb4264e2d.PNG"  width="650" height="250">
