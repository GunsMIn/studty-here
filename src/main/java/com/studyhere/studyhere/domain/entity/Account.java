package com.studyhere.studyhere.domain.entity;

import com.studyhere.studyhere.domain.dto.Notifications;
import com.studyhere.studyhere.domain.dto.Profile;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static javax.persistence.CascadeType.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor @ToString
@NoArgsConstructor
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE account SET deleted = true WHERE id = ?")
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = REMOVE)
    private List<AccountTag> accountTags = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY,cascade = REMOVE)
    private Set<Zone> zones = new HashSet<>();

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

    /**SoftDeleteColumn**/
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

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

    /**프로필 변경감지 수정**/
    public void changeProfile(Profile profile) {
        this.url = profile.getUrl();
        this.occupation = profile.getOccupation();
        this.location = profile.getLocation();
        this.bio = profile.getBio();
        this.profileImage = profile.getProfileImage();
    }
    /**이메일에 token 보낸지 1시간이 지났는지 check하는 메서드**/
    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    public boolean isValidateToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    /**비밀번호 변경**/
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    /**알림설정 변경**/
    public void changeNotifiacation(Notifications notifications) {
        this.studyCreatedByEmail = notifications.isStudyCreatedByEmail();
        this.studyCreatedByWeb = notifications.isStudyCreatedByWeb();
        this.studyEnrollmentResultByEmail = notifications.isStudyEnrollmentResultByEmail();
        this.studyEnrollmentResultByWeb = notifications.isStudyEnrollmentResultByWeb();
        this.studyUpdatedByEmail = notifications.isStudyUpdatedByEmail();
        this.studyUpdatedByWeb = notifications.isStudyUpdatedByWeb();
    }
    /**닉네임 변경**/
    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    /**회원이 study의 manager인지 check메서드
     * true이면 스터디의 manager
     * **/
    public boolean isManager(Study study) {
       return study.getManagers().contains(this);
    }
    /**연관관계 편의 메서드**/
    public void setAccountTags(AccountTag accountTags) {
        this.accountTags.add(accountTags);
        accountTags.setAccount(this);
    }

}
