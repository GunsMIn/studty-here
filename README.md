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



## 체크리스트

- [x] GitLab CI&CD pipeline 구축 : 새 버전 소프트웨어 관리 및 테스트 가능
    - GitLab Project가 업데이트 되었는지 확인하고 업데이트되어 있는 경우, 현재 컨테이너 제거 후 재 실행할 수 있도록 deploy.sh 작성
    - 미리 작성된 Dockerfile을 통해 build
    - crontab 기능을 활용하여 정기적으로 deploy.sh를 실행하도록 설정

- [x] User 회원가입 및 로그인 기능 구현
    - 회원가입 시, 아이디와 비밀번호를 입력받고, 중복된 아이디의 경우 회원가입 에러 발생
    - 회원가입 완료 시, 자동 로그인 기능 
    - 로그인 후, SMTP를 이용하여 이메일 인증 토큰을 발급(악의적인 이메일 발송을 막기위한 이메일 발송 1시간안에 1번 발송 가능 설계)
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
- [x] 스터디 등록,조회,수정
    - 스터디 등록 시 summernote 라이브러리를 사용하여 스터디 등록 기능
    - 스터디 조회 시 매니저여부,멤버여부,주제 조회,지역 조회 등 쿼리가 5개 나가는 것을 @EntityGraphe를 사용하여 쿼리 갯수 감소 및 시간 절약
    - 스터디 수정 시 Manager인지 check 후 일반회원이면 AccessDeniedException 발생(권한 없음 예외)
- [x] 스터디 배너 등록
    - 스터디 배너 미등록시 기본 배너 이미지 제공
    - 스터디 배너 jdenticon을 사용한 배너 이미지 등록,수정
- [x] 스터디 태그 , 지역 추가/삭제
    - 스터디 지역 Tagify 라이브러리 환경에서 Ajax를 이용한 비동기 처리(스터디 지역 추가, 삭제)
    - 스터디 태그 Tagify 라이브러리 환경에서 Ajax를 이용한 비동기 처리(스터디 태그 추가, 삭제)
    - 스터지 태그,지역 조회 시 @Entitygraphe를 사용하여 성능 최적화
- [x] 스터디 상태 설정 / 팀원 모집 / 스터디 삭제
    - 스터디 공개 / 스터디 종료 도메인 주도 설계 로직 체크 
    - 스터디가 DRAFT(공개 준비) 상태이면 스터디 공개 가능
    - 스터디가 Publised(스터디 오픈) 상태이면 스터디 종료 가능
    - 스터디 팀원모집 시작 / 중단 기능 (스터디 팀원 모집 시작 시 view 모집 버튼 활성화)
    - 스터디 삭제 (Soft delete)
- [x] 모임(Event) 생성 , 수정
    - 모임 생성 시 Validator를 사용한 글로벌 에러(접수 마감 시간, 모임 시작 시간, 모임 종료 시간 에러 체크 로직) 검사
    - 도메인 메서드와 spring expression를 사용한   모임 [참여]와 [참여 취소] 버튼이 상황과 조건에 따라 다르게 보이는 기능
    - 수정한 모임 제한인원이 기존 모임 참여 확정된 인원보다 작을 시 글로벌 에러 처리
- [x] 모임(Event) 참여
    - 선착순(FCFS) 방시의 모임과 참여 확정 인원이 모임 제한 인원보다 작을 경우 모임 참여 가능.
    - 선착순(FCFS) 모임에서 회원이 모임 참여를 취소할 시에 바로다음 대기인원 자동참여기능.
    - 관리자 확인(CONFIRMATIVE) 모임일 경우 모임 관리자가 승인 처리과정을 해줘야 모임 참여 가능
    - 모임을 갖은 후 출석체크 기능 구현(체크인 : 출석 , 체크아웃 : 결석)
- [x] 알림(Notification)
    - ApplicationEventPublisher와 스프링 @Async 기능을 사용해서 비동기 이벤트 기반으로 알림 처리.
    - 주요 로직 응답 시간에 영향을 주지 않기(코드를 최대한 주요 로직에 집중하고 알림 처리 로직은 분리.)
    - 핸들러 처리 이후, 뷰 랜더링 전에 스프링 웹 MVC HandlerInterceptor로 **읽지 않은 메시지가 있는지** 확인 후 알림서비스 제공    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
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
