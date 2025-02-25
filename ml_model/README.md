# ECG 신호를 통해 감정상태 판별

## 목표: WESAD 데이터셋을 이용하여 감정상태 판별:

    0 neutral
    1 stress
    2 amusement
    3 relax


*** 
## HRV의 개요

자율신경계는 체내외적인 환경변화에 대해서 내적 환경의 균형을 유지하는 역할을 하여 신체의 항상성 및 생명을 유지할 수 있도록 직접적으로 관여한다. 일반적으로 동일한 장기에 교감신경과 부교감신경이 함께 분포되어 서로 길항작용을 함으로써 신체의 균형을 이룬다. 안정 상태에서는 부교감신경이 우세해져서 심박동 수가 약 70회/분 로 감소해 있다가, 육체적, 정서적 스트레스가 가해지면 교감신경의 활성이 우세해져서 심박동수, 심박출량, 혈압 등이 증가한다.

심박동과 박동 사이의 간격(RR interval)은 안정을 취하고 있는 중에도 체내/외부 환경의 변화에 따라 끊임없이 변화하는데, 이러한 시간에 따른 심박의 주기적인 변화를 heart rate variability(HRV) 라고 한다. 일반적으로 HRV는 안정 상태일수록 크고 복잡하며, 스트레스를 받거나 운동을 하는 경우에는 규칙적이며 일정한 형태를 나타낸다. 

HRV에서 사용하는 수치는 시간 범위 분석으로는 평균 심박수, SDRR, RMSSD를 이용하고, 주파수 범위 분석(동성 심박 사이의 RR 간격의 변화를 파형 분석하여 각 주파수 영역의 신호가 상대적 어떤 강도(power spectral density)로 있는지 보는 방법) 으로는 VLF(very low frequency), LF(low frequency), HF(high frequency)를 사용한다. 


*** 
### 시간 범위 분석(time domain analysis)

: 측정하는 시간 동안 심박 간격의 변화 정도를 통계 처리하는 방식

1. SDRR(Standard Deviation of R-R interval) : 맥박 표준편차로, 외부환경에 대한 신체의 적응력을 나타내는 지표이다. 이 수치가 연령별 표준범위인 영역을 벗어나 좌측으로 점차 멀어지게 되면 스트레스에 대한 저항력이 점차 약해지게 된다.  심혈관계의 안정도와 더불어 자율신경계의 신체에 대한 제어 능력에 관한 정보를 제공하는 강력한 지표로 사용된다. 정상측정 범위는 16.9∼84로 높게 표시될수록 건강한 상태다.

2. RMSSD(Root Mean Square of Standard Deviation) : 평균 편차. 정상 심박동 사이의 연속적인 차이들의 제곱근이다. 부교감활성 정도를 확인하는 또 다른 방법으로 충분한 휴식이나 이완 상태에서 높게 나타나지만, 분노, 근심, 공포 상태에서 연령별 표준범위 영역을 벗어나 좌측으로 점차 멀어지게 된다. 정상측정 범위는 11∼51로 높게 표시될수록 건강한 상태다.

    
*** 
### 주파수 범위 분석(frequency domain analysis)

: R-R interval의 변화에 대한 주파수를 대역별로 분석하는 방법 

1. HF(High Frequency) : 부교감활성 성분. 충분한 휴식이나 이완 상태에서 높게 나타나지만, 분노, 근심, 공포 상태에서 연령별 표준범위 영역을 벗어나 좌측으로 점차 멀어지게 된다. 교감활성이 떨어진 상태가 오랫동안 지속되면 부교감활성도 교감활성의 영향을 받아 점차 떨어지게 되면서 스트레스의 누적정도가 더욱 심하게 된다. 정상측정 범위는 3.5∼6.7로 높게 표시될수록 건강한 상태다. 감소된 부교감신경(고주파수) 활성도는 많은 심장 질환과 공황 장애, 불안 또는 걱정의 스트레스를 가진 환자들에게서 발견되었다. nHF는 normalized HF로 nLF와 마찬가지로, 5분 이하의 짧은 구간에서는 VLF는 제외한다.

    
2. LF(Low Frequency) : 교감활성 성분. 긴장이나 흥분상태에서는 높게 나타나지만, 대부분의 경우 스트레스가 누적되기 시작하면 연령별 표준범위 영역을 벗어나 좌측으로 점차 멀어지게 된다. 정상측정 범위는 5.8∼7.8로 낮게 측정될수록 건강한 상태다. nLF는 normalized LF로 5분 이하의 짧은 구간에서는 VLF는 전체 전력에서 제외한다.
    

*** 
## 데이터셋
    
WESAD 데이터셋 : **Wearable Stress and Affect Detection Data Set**
    
- 웨어러블 기기를 이용한 스트레스 영향 감지를 위해 공개적으로 사용 가능한 데이터셋
- 혈액량 펄스, 심전도, 전기 피부 활동, 근전도, 호흡, 체온 및 3축 가속(blood volume pulse, electrocardiogram, electrodermal activity, electromyogram, respiration, body temperature, and three-axis acceleration)
- 이 중에서 ecg 데이터셋만 사용할 예정
- 모든 데이터는 700Hz에서 샘플링됨
- 데이터 세트는 세 가지의 다른 감정 상태를 가짐(baseline, stress, amusement)
- 이 데이터셋을 통해 학습시킨 모델을 이용하여 안드로이드 앱에서 스트레스 상태를 예측할 수 있도록 만들 예정
- 3가지 감정 상태로 학습시키는 경우보다 2가지(stress vs. non-stress)의 이진 분류 모델이 학습 정확도가 높을 것이기 때문에 2가지 감정 상태만을 판별하는 모델로 설계할 가능성도 있음.

*** 
## 데이터 전처리
    
- frequency domain analysis를 위해서는 pwm방식으로 받은 데이터를 푸리에 변환 해야 한다.
- 파이썬에서는 numpy의 np.fft.fft() 함수를 이용하여 푸리에 변환 할 수 있다.
- 매트랩을 이용하면 fft() 함수를 이용하여 할 수 있다.
- 머신러닝 모델을 학습시킬 데이터셋이 700Hz로 샘플링 되어 있기 때문에 실제로도 700Hz로 샘플링된 사용자의 ecg 데이터를 수집할 예정
    

*** 
## 머신러닝 모델

- 3가지 혹은 2가지 이진분류, 즉 분류 모델 이므로 XGBoost 혹은 LightGBM으로 학습시킬 예정
- 3가지 분류면 XGBoost 혹은 LightGBM, 이진 분류면 DecisionTree 이용할 것
- 프로그램의 간소화를 위해 WESAD 데이터셋의  ecg 데이터 중에서도 R-R interval, SDRR, RMSSD, LF, HF 등의 요소만 추출하여 사용할 것이다.

*** 
## 레이블

- stress vs non-stress로 구분하기 위해서는 위와 같은 방법으로 데이터 전처리를 한 후에  스트레스 상태에 피험자를 놓이게 한 후 레이블링 할 예정
- wesad 데이터셋과 최대한 유사하게 데이터를 받아올 필요가 있음



## 참고

https://www.kubios.com/hrv-analysis-methodhttps://oatext.com/heart-rate-variability-highlights-from-hidden-signals.php
https://oatext.com/heart-rate-variability-highlights-from-hidden-signals.php

