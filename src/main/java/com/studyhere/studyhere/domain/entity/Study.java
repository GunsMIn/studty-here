package com.studyhere.studyhere.domain.entity;

import com.studyhere.studyhere.domain.dto.StudyDescriptionForm;
import com.studyhere.studyhere.domain.userdetail.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE study SET deleted = true WHERE id = ?")
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;
    //인원모집 시간
    private LocalDateTime recruitingUpdatedDateTime;
    //현재 인원모집 여부
    private boolean recruiting;
    //공개
    private boolean published;
    //종료
    private boolean closed;
    //배너 사용 여부
    private boolean useBanner;

    private int memberCount;

    /**SoftDeleteColumn**/
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;


    public void addManager(Account account) {
        this.managers.add(account);
    }

    public void addTag(Tag tag) {
        //study에 태그 넣기
        this.getTags().add(tag);
    }

    /**
     * 가입여부 확인 메서드
     * study/view.html에서 study.isJoinable(#authentication.principal)의 메서드가 사용되는데
     * authentication.principal에는 userAccount가 담겨있다.
     * 따라서 userAccont.getAccont()로 account를 꺼낸다.
     *
     *
     * 1.study가 출시됐는지 여부
     * 2.study가 모집중인지 여부
     * 3.이미 존재하는 회원인지 check
     **/
    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.managers.contains(account) && !this.members.contains(account);
    }

    /**일반 참가인지 확인 메서드
     * true반환 -> 일반 참가자
     * **/
    public boolean isMember(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.members.contains(account);
    }
    /**Manager인지 확인 메서드
     * true반환 -> 매니저
     * **/
    public boolean isManager(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.managers.contains(account);
    }

    public boolean isManager(Account account) {
        return this.managers.contains(account);
    }

    public void updateDescription(StudyDescriptionForm studyDescriptionForm) {
        this.title = studyDescriptionForm.getTitle();
        this.shortDescription = studyDescriptionForm.getShortDescription();
        this.fullDescription = studyDescriptionForm.getFullDescription();
    }
    /**스터디경로가 한글로 들어오는 것을 대비한 encodePath**/
    public String encodePath() {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
    /**배너 사용유무(사용)**/
    public void changeEnableBanner() {
        this.useBanner = true;
    }
    /**배너 사용유무(미사용)**/
    public void changeDisableBanner() {
        this.useBanner = false;
    }

    /**배너 이미지 변경**/
    public void changeBannerImage(String image) {
        this.image = image;
    }

    /**배너 기본 이미지 설정**/
    public String getImage() {
        return image != null ? image : "/images/default_banner.png";
    }

    /**스터디 공개 설정 publish
     * 1.스터디가 공개되지 않은 상태여야함
     * 2.스터디가 종료되지 않은 상태여야함
     * **/
    public void publishStudyState() {
        if (!this.isPublished()&& !this.isClosed()) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("스터디를 공개할 수 없는 상태입니다. 스터디를 이미 공개했거나 종료된 스터디입니다.");
        }
    }

    /**스터디 마감하기 close
     * 1.스터디가 공개된상태여야함
     * 2.스터디가 종료되지 않은 상태여야함
     * **/
    public void closeStudyState() {
        if (this.isPublished() && !this.isClosed()) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("스터디를 종료할 수 없습니다. 스터디를 공개하지 않았거나 이미 종료한 스터디입니다.");
        }
    }

    /**스터디 인원 모집하기
     * 1.스터디가 오픈된상태여야함
     * 2.스터디가 마감되지 않은 상태여야함
     * 3.1시간에 한번만 바꿀 수 있는 조건
     * **/
    public void enableRecruiting() {
        if (checkRecruitCondition()) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        }else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }

    }

    /**스터디 인원 모집 마감
     * 1.스터디가 오픈된상태여야함
     * 2.스터디가 마감되지 않은 상태여야함
     * 3.1시간에 한번만 바꿀 수 있는 조건
     * **/
    public void disableRecruiting() {
        if (checkRecruitCondition()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        }else{
            throw new RuntimeException("인원 모집을 마감할 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }


    /**스터디 팀원 모집 사용가능 여부
     * 1.study가 오픈 상태인지 -> publish == ture
     * 2.recruitingUpdatedDateTime이 null인지
     * 3.recruitingUpdatedDateTime이 1시간 전인지
     * true이면 스터디 팀원 모집 변경가능
     * **/
    public boolean checkRecruitCondition() {
        return  this.published == true
                && this.recruitingUpdatedDateTime == null
                || this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }
    
    /**스터디 path 수정(변경감지)**/
    public void changePath(String newPath) {
        this.path = newPath;
    }
    /**스터디 title 수정(변경감지)**/
    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }

    /**스터디를 삭제할 수 있는지 여부 check
     * 1.스터디를 공개 했을 때 삭제 불가능
     * 2.스터디를 공개 하지 않았을 때는 삭제 가능
     * **/

    public boolean isRemovable() {
        return !this.published;
    }

    public void addMembers(Account account) {
        this.getMembers().add(account);
        this.memberCount++;
    }
    public void removeMembers(Account account) {
        this.getMembers().remove(account);
        this.memberCount--;
    }
}
