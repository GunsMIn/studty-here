package com.studyhere.studyhere.domain.entity;


import com.studyhere.studyhere.domain.userdetail.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    private Study study;

    @ManyToOne(fetch = LAZY)
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    /**모임 개설 시간**/
    @Column(nullable = false)
    private LocalDateTime createdDateTime;
    /**모임 등록 마감 시간**/
    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;
    /**모임 시작 일시**/
    @Column(nullable = false)
    private LocalDateTime startDateTime;
    /**모임 종료 일시**/
    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column
    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(value = STRING)
    private EventType eventType;


    /**
     * 모임 참여 가능 check 메서드 -> true 시 모임 참여 가능
     * 1.모집 등록 마감 시간이 현재 시간보다 뒤에있을경우
     * 2.이미 참석을 하지 않은 경우
     *
     * **/
    public boolean isEnrollableFor(UserAccount userAccount) {
        return !isAlreadyEnrolled(userAccount) && isNotClosed();
    }

    /**
     * 모임 참여 불가능 check 메서드 -> true 시 모임 참여 불가능
     * 1.모집 등록 마감 시간이 현재 시간보다 뒤에있을경우
     * 2.이미 참석을 한 경우
     *
     * **/
    public boolean isDisenrollableFor(UserAccount userAccount) {
        return isNotClosed() && isAlreadyEnrolled(userAccount);
    }

    /**모집 등록 마감 시간이 현재 시간보다 뒤에있을경우 check**/
    public boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    /**이미 참석한 경우 true 반환**/
    public boolean isAlreadyEnrolled(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }

    /****/
    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }

        return false;
    }
}
