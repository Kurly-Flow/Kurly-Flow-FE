![image](https://user-images.githubusercontent.com/35549958/186361611-c289ab0b-4e92-4f9f-b39c-b0964c53b1d2.png)
## 물류 처리에서 사람으로 인한 오류를 어떻게 줄일 수 있을까?
:point_right: 컬리의 물류센터는 주문된 상품을 집품(Picking)하고 포장(Packing)한 뒤 배송(Delivery)하는 과정 대부분을 작업자(Human) 분들이 직접 수행하고 있습니다.</br>
:point_right: 그러다 보니 휴먼에러(Human Error) - 잘못된 상품을 오피킹하거나, 오패킹하거나, 오배송하는 - 가 발생하고, 이로 인해 고객뿐만 아니라 물류 처리를 하는 담당하는 분들의 고생이 배가되기도 합니다.

## Kurly Flow - 마켓컬리 물류 센터 통합 운영 시스템
:four_leaf_clover: ‘Kurly Flow’’는 컬리 물류센터의 모든 공정에서 ‘관리자와 작업자 간의 소통을 향상’ 시킵니다.</br> 
:four_leaf_clover: 각 공정간의 흐름이 매끄럽게 이어질 수 있도록, 시간 지체와 오류 발생을 최소화합니다.  
1.  각 공정의 근무자는 문제 상황 발생 시, ‘관리자 호출’ 버튼을 눌러 빠르게 상황을 해결할 수 있다. 
2.  각 공정의 근무 TO는 시스템으로 관리되며, 작업자는 앱을 통해 희망 근무 권역을 입력할 수 있고, 확정된 근무지를    
    확인할 수 있다.
3.  피킹 작업자는, 토트가 일정 무게 이상을 넘어갈 때 ‘토트 변경 알림’을 받아 토트를 미인식하는 실수를 방지하며,  
    실수가 발생했을 경우, ‘토트 옮겨담기’ 버튼을 통해 다른 토트로 상품을 재등록 할 수 있다.  
4.  엔드 작업자는 각 상자의 작업률을 열람할 수 있으며, 송장을 직접 재발행 할 수 있고, ‘상자 적재 요청’ 버튼을 통해 
    부족한 택배상자를 공급받을 수 있다. 
5.  패킹 작업자는 2차 포장 대상 상품과, 포장재 종류를 시스템으로부터 안내받아, 오류 없이 2차 포장을 할 수 있다. 

## 스크린샷
<img src="https://user-images.githubusercontent.com/35549958/186371052-3ebc5833-b2c3-4c08-985e-5787fe35baed.png" width="300px" ></img> 
<img src="https://user-images.githubusercontent.com/35549958/186371258-af46e96c-ddc6-4a7c-b43a-af7ee65909bc.png" width="300px" ></img> 
<img src="https://user-images.githubusercontent.com/35549958/186371276-c2935c5d-7de8-41c0-8f66-d6ab3992b9ce.png" width="300px" ></img> 
<img src="https://user-images.githubusercontent.com/35549958/186371300-b14a29d8-9a47-44ee-8529-1f9b20cb98d3.png" width="300px" ></img> 
<img src="https://user-images.githubusercontent.com/35549958/186371828-468945b7-f54e-4a7e-9bd3-04e6c548a182.png" width="300px" ></img> </br>
<img src="https://user-images.githubusercontent.com/35549958/186371368-b99501ff-a6a8-49f1-bb52-e6068969e488.png" width="450px" ></img>
<img src="https://user-images.githubusercontent.com/35549958/186371382-d9dddd75-1384-442d-aa82-dc004e3e92be.png" width="450px" ></img>

## Dependencies
```
dependencies {

    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.2'
    implementation 'com.google.android.material:material:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.work:work-runtime-ktx:2.5.0'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
    implementation 'androidx.work:work-runtime-ktx:2.7.1'

    //retrofit2
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.retrofit2:converter-scalars:2.9.0'

    //okhttp3
    implementation 'com.squareup.okhttp3:okhttp:4.9.1'
    implementation 'com.squareup.okhttp3:okhttp-urlconnection:4.9.1'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.8.0'

    //glide
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'

    //fcm
    implementation platform('com.google.firebase:firebase-bom:28.4.1')
    implementation 'com.google.firebase:firebase-messaging-ktx'
    implementation 'com.google.firebase:firebase-analytics-ktx'
}
```

## Application Version
- minSdk 23
- targetSdk 31
