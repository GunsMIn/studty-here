# studty-here
스터디를 구하는 사이트
## 개발환경
<br>

- **Java 11**
- **Build** : Gradle 7.6
- **Framework** : Springboot 2.7.7
- **CI & CD** : GitLab
- **Server** : AWS EC2
- **Deploy** : Docker
- **IDE** : IntelliJ
<br>

## 구현 완료
**Function** | **완료** | 
:------------ | :-------------| 
**회원가입시 이메일 check Token 발급, 이메일 발송, 유효성 검사** | :heavy_check_mark: |  
**Spring Security 인증 / 인가 필터 구현** | :heavy_check_mark: |  
**로그인 / 로그아웃 / 자동로그인(RememberMe)** | :heavy_check_mark: |  
**프로필 사진 등록** | :heavy_check_mark: |  
**프로필 변경 , 비밀번호 변경, 알림 설정** | :heavy_check_mark: |  
**관심주제(Tag) 등록,삭제,자동완성** | :heavy_check_mark: |  
**CSV파일 지역(Zone)객체로** | :heavy_check_mark: |  
**지역 정보(Zone) 등록,삭제** | :heavy_check_mark: | 
**AWS EC2 서버 Docker 배포** | :heavy_check_mark: |  
**Gitlab CI & Crontab CD** | :heavy_check_mark: 

## 체크리스트

- [x] GitLab CI&CD pipeline 구축 : 새 버전 소프트웨어 관리 및 테스트 가능
    - GitLab Project가 업데이트 되었는지 확인하고 업데이트되어 있는 경우, 현재 컨테이너 제거 후 재 실행할 수 있도록 deploy.sh 작성
    - 미리 작성된 Dockerfile을 통해 build
    - crontab 기능을 활용하여 정기적으로 deploy.sh를 실행하도록 설정

- [x] User 회원가입 및 로그인 기능 구현
    - 회원가입 시, 아이디와 비밀번호를 입력받고, 중복된 아이디의 경우 회원가입 에러 발생
    - 회원가입 완료 시, 자동 로그인 기능 
    - 로그인 후, SMTP를 이용하여 이메일 인증 토큰을 발급
- [x] Spring security
    - Spring security의 form login 방식을 채택
    - 인증 / 인가 
    - rememberMe를 이용한 Remember-Me 토큰 쿠키 발급 -> 로그인 유지 
- [x] 프로필
    - 프로필 입력 기능 : 한 줄 소개,링크,직업.활동 지역 (modelMapper를 이용한 view 데이터 전송)
    - 프로필 사진 등록 : jdenticon을 이용한 프로필 사진 등록
    - 비밀번호 변경 : Validator를 이용해 유효성 검사 후 비밀번호 변경
    - 알림 설정 기능 : 스터디에 대한 알림 설정(웹으로 받기,이메일로 받기 선택)
- [x] 관심주제와 활동지역 선택 기능 
    - 관심 주제 : Tagify 라이브러리 환경에서 Ajax를 이용한 비동기 처리(관심주제 추가,삭제)
    - 활동 지역 : zones_kr.csv 파일을 자바 객체로 Parsing하여 지역을 데이터베이스에 저장, Tagify 라이브러리 환경에서 Ajax를 이용한 비동기 처리(활동 지역 추가, 삭제)
- [x] 스터디 등록
    - 스터디 등록 시 summernote 라이브러리를 사용하여 스터디 등록 기능
    - 스터디 조회 시 매니저여부,멤버여부,주제 조회,지역 조회 등 쿼리가 5개 나가는 것을 @EntityGraphe를 사용하여 쿼리 갯수 감소 및 시간 절약
    - 스터디 수정 시 Manager인지 check 후 일반회원이면 AccessDeniedException 발생(권한 없음 예외)
