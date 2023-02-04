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

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
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

    public void addManager(Account account) {
        this.managers.add(account);
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
    /**스터디가 한글로 들어오는 것을 대비한 encodePath**/
    public String encodePath() {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
