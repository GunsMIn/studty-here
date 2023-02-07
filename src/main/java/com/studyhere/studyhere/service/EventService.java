package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.dto.EventForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Enrollment;
import com.studyhere.studyhere.domain.entity.Event;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.events.event.EnrollmentAcceptedEvent;
import com.studyhere.studyhere.domain.events.event.EnrollmentRejectedEvent;
import com.studyhere.studyhere.domain.events.event.StudyUpdateEvent;
import com.studyhere.studyhere.repository.EnrollmentRepository;
import com.studyhere.studyhere.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service @Slf4j
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Event createEvent(Study study, EventForm eventForm, Account account) {
        //연관관계 값들 넣어줌🔽(builder 사용)
        Event event = eventForm.of(account,study);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임을 만들었습니다."));
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event findEvent(Event event) {
        Event findEvent = eventRepository.findEventById(event.getId());
        if (findEvent == null) {
            throw new IllegalStateException("해당 모임은 존재하지 않습니다.");
        }
        return findEvent;
    }
    
    /**event 수정**/
    public void change(Event event, EventForm eventForm) {
        event.changeEvent(eventForm);
        event.acceptWaitingList();
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임 정보를 수정했으니 확인하세요."));
    }

    public void delete(Event findEvent) {
        eventRepository.delete(findEvent);
        eventPublisher.publishEvent(new StudyUpdateEvent(findEvent.getStudy(),
                "'" + findEvent.getTitle() + "' 모임을 취소했습니다."));
    }

    /**
     * 모임 참여하기 -> 등록하기
     * 1.이미 참여하였는지 확인
     * 2.선착순 모임의 가입일 경우 + 제한 인원이 아직 있을 경우 Accepted(확정)
     * **/
    public void createEnrollment(Account account, Event event) {
        //이미 참여한 회원이 아닐 때
        if (!enrollmentRepository.existsByAccountAndEvent(account, event)) {
            Enrollment newEnrollment = Enrollment.builder()
                    .account(account)
                    .event(event)
                    .accepted(event.confirmStatus()) // accepted는 확정이다. 1.선착순일때 2.참여 제한 인원이 확정된 인원보다 클 때
                    .enrolledAt(LocalDateTime.now())
                    .build();
            enrollmentRepository.save(newEnrollment);
        }
    }

    /**
     * 모임 참가 취소하기
     * 1.회원과 event가 해당 등록에 존재해야함
     * 2.soft delete
     * 3.선착순 모임일 시 대기 인원 자동 참여
     * **/
    public void cancelEnrollment(Account account, Event event) {
        Enrollment enrollment = enrollmentRepository.findByAccountAndEvent(account, event);
        //해당 등록이 아직 참석하지 않았을 대
        if (!enrollment.isAttended()) {
            enrollmentRepository.delete(enrollment);
            event.removeEnrollment(enrollment);
            //해당 등록이 삭제 되면서 다음 대기 유저를 확정 상태로 만들어주어야함🔽
            acceptNextWaitingEnrollment(event);
        }
    }

    /**대기 인원 자동 참여**/
    private void acceptNextWaitingEnrollment(Event event) {
        if (event.isAbleToAcceptWaitingEnrollment()) {
            Enrollment firstEnrollment = event.getFirstEnrollment();
            if (firstEnrollment != null) {
                firstEnrollment.changeAccepted(true);
            }
        }
    }

    /**알림 이벤트 발생**/
    /**등록 승인**/
    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.acceptConfirmativeType(enrollment);
        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
    }

    /**알림 이벤트 발생**/
    /**등록 거절**/
    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.rejectConfirmativeType(enrollment);
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
    }

    /**모임 출석 체크인**/
    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.changeAttended(true);
    }

    /**모임 미출석 체크아웃**/
    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.changeAttended(false);
    }
}
