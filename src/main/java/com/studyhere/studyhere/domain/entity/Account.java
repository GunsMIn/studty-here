package com.studyhere.studyhere.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    /**이메일 인증이 된 계정인지 확인 플래그**/
    private boolean emailVerified;

    /**이메일 검증시 사용 할 토큰 값**/
    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    /**프로필 정보**/
    private String bio;

    private String url;
    /**직업**/
    private String occupation;
    /**거주지**/
    private String location;

    /**프로필 이미지**/
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;
    /**알림 설정 수신 플래그**/
    private boolean studyCreatedByEmail;
    /**웹으로 받을 것인가**/
    private boolean studyCreatedByWeb = true;
    /**가입신청 결과**/
    private boolean studyEnrollmentResultByEmail;
    /**가입신청 결과 웹**/
    private boolean studyEnrollmentResultByWeb = true;
    /**스터디 변경 알람**/
    private boolean studyUpdatedByEmail;
    /**스터디 변경 웹**/
    private boolean studyUpdatedByWeb = true;

   /* @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();*/

    /**계정 생성시 UUID를 사용하여 랜덤한 토큰 값 발급**/
    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    public boolean isValidateToken(String token) {
        return this.emailCheckToken.equals(token);
    }
}
