package com.studyhere.studyhere.domain.entity;


import com.studyhere.studyhere.domain.dto.EventForm;
import com.studyhere.studyhere.domain.userdetail.UserAccount;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder @ToString
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE event SET deleted = true WHERE id = ?")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    private Study study;

    @ManyToOne(fetch = LAZY)
    private Account createdBy;

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    /**모임 개설 시간**/
    @Column(nullable = false)
    private LocalDateTime createdDateTime;
    /**접수 마감날짜**/
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

    @Enumerated(value = STRING)
    private EventType eventType;

    /**SoftDeleteColumn**/
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;


    /**
     * 모임 참여가 가능한 경우 = true
     * 모임 참여 가능 check 메서드 -> true 시 모임 참여 가능
     * 1.모집 등록 마감 시간이 현재 시간보다 뒤에있을경우
     * 2.이미 참석을 하지 않은 경우
     *
     * **/
    public boolean isEnrollableFor(UserAccount userAccount) {

        return !isAlreadyEnrolled(userAccount) && isNotClosed();
    }

    /**
     * 모임 참여 취소가 가능한경우 = true
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

    /**이미 참석을 한 경우 = true**/
    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }
        return false;
    }

    /**모임의 남은 자리 check메서드**/
    public int numberOfRemainSpots() {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.isAccepted()) {
                limitOfEnrollments--;
            }
        }
        return limitOfEnrollments;
        //return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    /**모임 확정 된 사람 count**/
    public long confirmNumber() {
        return this.getEnrollments()
                .stream().filter(Enrollment::isAccepted).count();
    }

    /**모임 수정 메서드(변경감지)**/
    public void changeEvent(EventForm eventForm) {
        this.title = eventForm.getTitle();
        this.description = eventForm.getDescription();
        this.eventType = eventForm.getEventType();
        this.endEnrollmentDateTime = eventForm.getEndEnrollmentDateTime();
        this.startDateTime = eventForm.getStartDateTime();
        this.endDateTime = eventForm.getEndDateTime();
    }

    /**event가 확정된 상태
     * true 이면 Enrollement(accepted)가 true
     * 1.선착순이면서
     * 2.현재 참여 확정 인원이 < 제한인원보다 작을 때
     * **/
    public boolean confirmStatus() {
        return this.eventType.equals(EventType.FCFS) && this.limitOfEnrollments > this.confirmNumber();
    }

    /** true일 시 신청(enrollment) 수락 가능
     * 1. 모임 타입이 관리자 확인일 때
     * 2. 등록이 해당 등록을 가지고 있을때
     * 3. 아직 참석하지 않았을 때
     * 4. 아직 수락하지 않았을 때 **/
    public boolean canAccept(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    /** true일 시  등록 거절 가능
     * 1. 모임 타입이 관리자 확인일 때
     * 2. 등록이 해당 등록을 가지고 있을때
     * 3. 아직 참석하지 않았을 때
     * 4. 수락한 상태일 때 거절 가능**/
    public boolean canReject(Enrollment enrollment) {
        return  this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    /**선착순 모임일 시 대기인원 추가 가능 조건**/
    public boolean isAbleToAcceptWaitingEnrollment() {
        return this.eventType.equals(EventType.FCFS)
                && this.limitOfEnrollments > this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }
    
    /**첫번째 등록을 가져오는 메서드**/
    public Enrollment getFirstEnrollment() {
        for (Enrollment enrollment : this.enrollments) {
            if (!enrollment.isAccepted() ) {
                return enrollment;
            }
        }
        return null;
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
    }
}
