# study-here
## 스터디 모집 및 참여,스터디 모임 개설,실시간 알림 기능,비동기 방식을 이용한 등록 기능
### [스터디히어 프로젝트 시연영상 링크](https://www.youtube.com/watch?v=zPXeYeUSgkg&t=229s) 
- **직접 스터디를 개설하고 스터디원들과 모임을 갖을 수 있는 서비스**
- **관심주제에 맞는 선호지역에 맞게 스터디와 스터디 모임을 검색 및 추천해주는 서비스** <br>

</p>

### 프로젝트 결과
[📎스터디히어 프로젝트 시연영상 링크](https://www.youtube.com/watch?v=zPXeYeUSgkg&t=229s)
<br>
[📎 서비스 UI](http://ec2-13-125-252-76.ap-northeast-2.compute.amazonaws.com:8080/)

📌 프로젝트 기술스택
- 에디터 : Intellij Ultimate
- 개발 툴 : SpringBoot 2.7.6
- 자바 : JAVA 11
- 빌드 : Gradle 6.8
- 서버 : AWS EC2
- 데이터베이스 : MySql 8.0
- 필수 라이브러리 : SpringBoot Web, MySQL, Spring Data JPA, Lombok, Spring Security

# ERD 다이어그램
![스터디히어](https://user-images.githubusercontent.com/104709432/217971669-619734fb-f0dc-48d7-b09b-60b078971530.png)

## 체크리스트

- [x] **User 회원가입 및 로그인 기능 구현** <br>
   >[프로젝트 : **Spring security 로그인/로그아웃** 정리본](https://velog.io/@guns95/Spring-security%EC%97%90%EC%84%9C%EC%9D%98-%EB%A1%9C%EA%B7%B8%EC%9D%B8%EB%A1%9C%EA%B7%B8%EC%95%84%EC%9B%83)
     
     >[프로젝트 : 이메일 인증 코드 보내기(SpringBoot, **SMTP**)](https://velog.io/@guns95/%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%9D%B8%EC%A6%9D-%EC%BD%94%EB%93%9C-%EB%B3%B4%EB%82%B4%EA%B8%B0SpringBoot-SMTP)   

    
    - 회원가입 시, 아이디와 비밀번호를 입력받고, **회원가입시 중복된 아이디 검사**
    - **회원가입 완료 시, 자동 로그인 기능** 
    - 로그인 후, **SMTP를 이용하여 이메일 인증 토큰을 발급**(악의적인 이메일 발송을 막기위한 이메일 발송 **1시간안에 1번 발송** 가능)
- [x] **Spring security**
   >[프로젝트 : **Custom AuthenticationPrincipal**를 사용한 인증](https://velog.io/@guns95/Custom-AuthenticationPrincipal%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%9C-%EC%9D%B8%EC%A6%9D)
   
   >[프로젝트 : Spring Security **Remeber Me**(로그인 정보 기억하기)](https://velog.io/@guns95/Spring-Security-Remeber-Me%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EC%A0%95%EB%B3%B4-%EA%B8%B0%EC%96%B5%ED%95%98%EA%B8%B0)
   
    - Spring security의 form login 방식을 채택
    - Spring security 인증 / 인가 
    - **rememberMe를 이용한 Remember-Me 토큰 쿠키 발급 -> 로그인 유지** 
- [x] **프로필**
    - 프로필 입력 기능 : 한 줄 소개,링크,직업.활동 지역 (modelMapper를 이용한 view 데이터 전송)
    - 프로필 사진 등록 : j**denticon을 이용한 프로필 사진 등록**
    - 비밀번호 변경 : Validator를 이용해 유효성 검사 후 비밀번호 변경
    - 알림 설정 기능 : 스터디에 대한 알림 설정(웹으로 받기,이메일로 받기 선택)
- [x] **관심주제와 활동지역 선택 기능**
 
     >[프로젝트 : Ajax를 사용한 관심주제(tag) 등록 ](https://velog.io/@guns95/tagify%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EA%B4%80%EC%8B%AC%EC%A3%BC%EC%A0%9C-%EB%93%B1%EB%A1%9D)
     
     >[프로젝트 : 관심주제(tag)와 지역(zone) **자동완성기능**  ](https://velog.io/@guns95/StreamAjax%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%9C-%EC%9E%90%EB%8F%99%EC%99%84%EC%84%B1-%EA%B8%B0%EB%8A%A5)
     
     >[프로젝트 : 국내지역정보 CSV 파일을 엔티티 객체(Zone)으로 Parsing하기 ](https://velog.io/@guns95/tagify%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EA%B4%80%EC%8B%AC%EC%A3%BC%EC%A0%9C-%EB%93%B1%EB%A1%9D)
     
    - 관심 주제 : Tagify 라이브러리 환경에서 **Ajax를 이용한 비동기 처리**(관심주제 추가,삭제)
    - 활동 지역 : zones_kr.csv 파일을 자바 객체로 **Parsing하여 지역을 데이터베이스에 저장**, Tagify 라이브러리 환경에서 **Ajax를 이용한 비동기 처리**(활동 지역 추가, 삭제)
- [x] **스터디 등록,조회,수정**
    - 스터디 등록 시 summernote 라이브러리를 사용하여 스터디 등록 기능
    - 스터디 조회 시 매니저여부,멤버여부,주제 조회,지역 조회 등 **쿼리가 5개 나가는 것을 @EntityGraphe를 사용하여 쿼리 갯수 감소 및 시간 절약**
    - 스터디 수정 시 Manager인지 check 후 일반회원이면 AccessDeniedException 발생(권한 없음 예외)
- [x] **스터디 배너 등록**
    - 스터디 배너 미등록시 기본 배너 이미지 제공
    - 스터디 배너 jdenticon을 사용한 배너 이미지 등록,수정
- [x] **스터디 태그 , 지역 추가/삭제**
    - 스터디 지역 Tagify 라이브러리 환경에서 **Ajax를 이용한 비동기 처리**(스터디 지역 추가, 삭제)
    - 스터디 태그 Tagify 라이브러리 환경에서 **Ajax를 이용한 비동기 처리**(스터디 태그 추가, 삭제)
    - 스터지 태그,지역 조회 시 **@Entitygraphe를 사용하여 성능 최적화**
- [x] **스터디 상태 설정 / 팀원 모집 / 스터디 삭제**
    - 스터디 공개 / 스터디 종료 도메인 주도 설계 로직 체크 
    - 스터디가 DRAFT(공개 준비) 상태이면 스터디 공개 가능
    - 스터디가 Publised(스터디 오픈) 상태이면 스터디 종료 가능
    - 스터디 팀원모집 시작 / 중단 기능 (스터디 팀원 모집 시작 시 view 모집 버튼 활성화)
    - 스터디 삭제 (Soft delete)
- [x] **모임(Event) 생성 , 수정**
    - 모임 생성 시 Validator를 사용한 (접수 마감 시간, 모임 시작 시간, 모임 종료 시간 에러 체크 로직) 시간 유효성 검사
    - 도메인 메서드와 spring expression를 사용한   모임 [참여]와 [참여 취소] 버튼이 상황과 조건에 따라 다르게 보이는 기능
    - 수정한 모임 제한인원이 기존 모임 참여 확정된 인원보다 작을 시 글로벌 에러 처리
- [x] **모임(Event) 참여**
   >[프로젝트 : 모임(event) 참여 취소 시 대기회원을 모임에 **자동 참여 기능**](https://velog.io/@guns95/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EB%AA%A8%EC%9E%84-%EC%B0%B8%EA%B0%80-%EC%B7%A8%EC%86%8C-%EC%8B%9C-%EB%8C%80%EA%B8%B0-%ED%9A%8C%EC%9B%90-%EC%9E%90%EB%8F%99-%EB%AA%A8%EC%9E%84-%ED%99%95%EC%A0%95-%EB%A1%9C%EC%A7%81-vlz068n3)
   
    - 선착순(FCFS) 방식의 모임과 관리자 확인 모임(CONFIRMATIVE)에서 **참여 확정 인원이 모임 제한 인원보다 작을 경우 모임 참여 가능**
    - 선착순(FCFS) 모임에서 회원이 모임 참여를 취소할 시에 바로 다음 대기인원 자동참여기능.
    - **관리자 확인(CONFIRMATIVE) 모임일 경우 모임 관리자가 승인 처리과정을 해줘야 모임 참여 가능**
    - 모임을 갖은 후 **출석체크 기능 구현(체크인 : 출석 , 체크아웃 : 결석)**
- [x] **알림(Notification)**
   >[프로젝트 : ApplicationEventPublisher와 스프링 @Async을 사용한 비동기 이벤트기반으로 알림 처리](https://velog.io/@guns95/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-ApplicationEventPublisher%EC%99%80-%EC%8A%A4%ED%94%84%EB%A7%81-Async%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%9C-%EB%B9%84%EB%8F%99%EA%B8%B0-%EC%9D%B4%EB%B2%A4%ED%8A%B8%EA%B8%B0%EB%B0%98%EC%9C%BC%EB%A1%9C-%EC%95%8C%EB%A6%BC-%EC%B2%98%EB%A6%AC)
   
   >[프로젝트 : HandlerInterceptor를 이용한 읽지 않은 알람 처리](https://velog.io/@guns95/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-HandlerInterceptor%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%9D%BD%EC%A7%80-%EC%95%8A%EC%9D%80-%EC%95%8C%EB%9E%8C-%EC%B2%98%EB%A6%AC)
   
    - ApplicationEventPublisher와 **스프링 @Async 기능을 사용해서 비동기 이벤트 기반으로 알림 처리**.
    - **주요 로직 응답 시간에 영향을 주지 않기(코드를 최대한 주요 로직에 집중하고 알림 처리 로직은 분리.)**
    - 핸들러 처리 이후, 뷰 랜더링 전에 스프링 웹 MVC HandlerInterceptor로 **읽지 않은 메시지가 있는지** 확인 후 알림서비스 제공    
    - **QueryDsl Predicate를 회원의 관심주제와 선호지역이 일치하는 스터디가 오픈했을 때 알림 발송** 
- [x] **스터디 검색**
   >[프로젝트 : Querydsl 검색조건에서의 N+1문제 해결 정리본](https://velog.io/@guns95/Querydsl-%EA%B2%80%EC%83%89%EC%A1%B0%EA%B1%B4%EC%97%90%EC%84%9C%EC%9D%98-N1%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%EB%A1%9C%EA%B7%B8)
   
    - 스터디 검색 시 스터디 제목, 스터디 지역, 스터디 태그으로 검색이 가능하다.
    - **QueryDsl로 검색을 구현 left(outer) join + fetchJoin + distinct 방식으로 N+1 문제 해결** 


<hr>

#### 메인페이지
![](https://velog.velcdn.com/images/guns95/post/3235eed4-0211-4769-a6d7-eabab059b4a0/image.PNG)

#### 회원가입페이지
> - 닉네임과 이메일 중복체크 
> - 이메일 형식 체크 / 패스워드 형식 체크

![](https://velog.velcdn.com/images/guns95/post/865b4188-24ea-453b-9630-c497ef1200ca/image.PNG)

#### 관심주제 등록 페이지
- Ajax를 이용한 비동기 방식의 관심주제 등록
![](https://velog.velcdn.com/images/guns95/post/274623e5-7bce-422d-94c2-abb3cc783c58/image.PNG)

#### 선호지역 등록 페이지
- Ajax를 이용한 비동기 방식의 선호지역 등록
![](https://velog.velcdn.com/images/guns95/post/3922d24e-7243-48d8-a0e6-133f6f155455/image.PNG)

#### 로그인 후 메인페이지
>- 선호지역과 관심주제에 맞게 스터디 추천 기능 
>- 관리중인 스터디 / 참여중인 스터디 보기 기능

![](https://velog.velcdn.com/images/guns95/post/87906846-539c-42a9-8d03-f3c42df5178d/image.PNG)


#### 프로필 등록 페이지 
>- 한 줄 자기소개 등록
>- 블로그 링크 , 깃 허브 링크 등록
>- 직업, 활동 지역 등록
>- 프로필 사진 등록

![](https://velog.velcdn.com/images/guns95/post/ce94545b-2638-4f67-a195-8bf22f117a56/image.PNG)

#### 프로필 페이지 (프로필 등록 후)
![](https://velog.velcdn.com/images/guns95/post/e30f29a0-6549-4a10-9948-bed09ff26289/image.PNG)


#### 알림 설정 페이지
>- 주요 활동 지역과 관심있는 주제의 스터디가 개설되었을때 알림
>- 스터디 모임 참가 신청 결과 알림
>- 참여중인 스터디에 업데이트 알림
위 3개의 알림을 웹 알림과 이메일알림 선택 가능

![](https://velog.velcdn.com/images/guns95/post/b5740ff4-4a26-4d86-97b4-c94a27625359/image.PNG)

#### 패스워드 변경 페이지
![](https://velog.velcdn.com/images/guns95/post/cc40425c-ba1e-4123-9b6f-80b72351fd98/image.PNG)

#### 닉네임 변경, 계정 삭제 페이지
![](https://velog.velcdn.com/images/guns95/post/6474a982-33e6-41b9-b588-28d5d7a59903/image.PNG)

#### 스터디 개설 페이지
>- 스터디 이름, 짧은 소개, 상세 소개 등록
>- 스터디 등록 후 관심주제와 스터디 지역 선택 가능

![](https://velog.velcdn.com/images/guns95/post/0fee203d-3c5d-49a7-8d3c-7b3482dee68b/image.PNG)

#### 스터디 등록 후 상세페이지
>- 스터디 소개 페이지
>- 스터디 구성원 보기 페이지
>- 스터디 모임 페이지
>- 스터디 설정 페이지 **(관리자만 확인 가능)**

![](https://velog.velcdn.com/images/guns95/post/ea74c274-aead-4f30-bb3c-64de96824577/image.PNG)

#### 스터디 구성원 페이지
![](https://velog.velcdn.com/images/guns95/post/abfc2531-4b14-420c-a933-364b198ee7c5/image.PNG)


#### 스터디 수정 페이지 (관리자만 가능)
![](https://velog.velcdn.com/images/guns95/post/7cf1fc73-2ab7-40df-9178-8d9b17d3c1ee/image.PNG)

#### 스터디 배너 등록 페이지(관리자만 가능)
> - 스터디 히어 기본 배너 이미지 제공 및 사용 가능
>- 커스텀 배너 이미지 사용 가능 

**배너 적용전**
![](https://velog.velcdn.com/images/guns95/post/5df34935-7ca0-45aa-8d22-befb64cbfa67/image.PNG)
**배너 적용 후**
![](https://velog.velcdn.com/images/guns95/post/68481884-05a8-4a1e-9a2c-edded50d416f/image.PNG)

#### 개설 스터디 관심주제, 선호지역 등록
>**관심주제 등록(Ajax 비동기 방식 등록)**
![](https://velog.velcdn.com/images/guns95/post/4e822257-d5c6-47a5-93bd-723924d322bd/image.PNG)

>**관심주제 등록(Ajax 비동기 방식 등록)**
![](https://velog.velcdn.com/images/guns95/post/32ddb9b4-8ae4-4426-9560-068b40ead261/image.PNG)

#### 스터디 공개 및 팀원 모집 시작 가능페이지
>- 스터디 비공개 <-> 공개 전환 기능
>- 스터디 팀원 모집 시작 기능

![](https://velog.velcdn.com/images/guns95/post/0ec42d9a-4de4-4da6-b3d0-a682b2cb3ec2/image.PNG)

#### 스터디 모임 개설 페이지
>- 모집 방법 선택가능
>	- 선착순 모집
>	- 관리잠 참여 확인 모집
>- 모집 인원 , 등록 마감 일시 , 모임 시작 일시 , 모임 종료 일시 선택

![](https://velog.velcdn.com/images/guns95/post/1f01dc99-3fa0-449d-899d-081bb743161a/image.PNG)

#### 모임 상세 페이지 (모임 개설 후)
![](https://velog.velcdn.com/images/guns95/post/bd1c1850-8ab3-4f8a-b891-3185881b6296/image.PNG)


#### 모임 리스트 페이지
- 새모임과 지난 모임 모두 확인 가능
![](https://velog.velcdn.com/images/guns95/post/138318cb-bb25-456e-b147-1567ef6b35b5/image.PNG)

#### 모임 참여 신청 페이지
![](https://velog.velcdn.com/images/guns95/post/943598d6-5e34-4826-ba70-23d7d42705f1/image.PNG)

#### 스터디 검색 페이지
>- 스터디 검색 기능(스터디 제목 , 스터디 지역 , 스터디 주제로 검색)
>- 검색 키워드 하이라이트 처리

![](https://velog.velcdn.com/images/guns95/post/24611dea-9b17-47f3-88b2-700b76dedf4b/image.PNG)

#### 실시간 알림 페이지
>- 1. 참여중인 스터디 알림
>- 2. 관심 주제 스터디, 선호 지역 스터디 개설 시 알림
>- 3. 참여중인 모임 알림
>- 읽지 않은 알림 / 읽은 알림 확인 가능
>- 읽은 알림 삭제 기능

![](https://velog.velcdn.com/images/guns95/post/1d8823ac-a6f9-40d3-a2ef-2c9b60fa96a5/image.PNG)

## [🔼 스터디히어 프로젝트 시연영상 링크](https://www.youtube.com/watch?v=zPXeYeUSgkg&t=229s) 
