package com.studyhere.studyhere.domain.events.event;

import com.studyhere.studyhere.domain.entity.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyCreatedEvent {
    private final Study study;
}
