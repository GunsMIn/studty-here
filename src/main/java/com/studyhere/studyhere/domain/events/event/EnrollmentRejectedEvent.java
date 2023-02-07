package com.studyhere.studyhere.domain.events.event;

import com.studyhere.studyhere.domain.entity.Enrollment;
import com.studyhere.studyhere.domain.events.event.EnrollmentEvent;

public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청을 거절했습니다.");
    }
}