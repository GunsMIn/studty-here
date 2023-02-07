package com.studyhere.studyhere.domain.events.event;

import com.studyhere.studyhere.domain.entity.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class EnrollmentEvent {

    protected final Enrollment enrollment;

    protected final String message;

}