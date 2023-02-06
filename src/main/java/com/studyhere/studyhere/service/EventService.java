package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.dto.EventForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Enrollment;
import com.studyhere.studyhere.domain.entity.Event;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.repository.EnrollmentRepository;
import com.studyhere.studyhere.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Event createEvent(Study study, EventForm eventForm, Account account) {
        //연관관계 값들 넣어줌🔽(builder 사용)
        Event event = eventForm.of(account,study);
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
    }

    public void delete(Event findEvent) {
        eventRepository.delete(findEvent);
    }

    /**
     * 모임 참여하기 -> 등록하기
     * 1.이미 참여하였는지 확인
     * 2.
     * **/
    public void createEnrollment(Account account, Event event) {
        //이미 참여한 회원이 아닐 때
        if (!enrollmentRepository.existsByAccountAndEvent(account, event)) {
            Enrollment enrollment = new Enrollment();
            enrollment.builder()
                    .account(account)
                    .event(event)
                    .accepted(event.confirmStatus()) // accepted는 확정이다. 1.선착순일때 2.참여 제한 인원이 확정된 인원보다 클 때
                    .enrolledAt(LocalDateTime.now())
                    .build();
            enrollmentRepository.save(enrollment);
        }
    }
}
