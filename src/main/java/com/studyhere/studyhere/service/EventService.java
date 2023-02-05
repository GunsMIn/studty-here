package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.dto.EventForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Event;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Study study, EventForm eventForm, Account account) {
        //연관관계 값들 넣어줌🔽(builder 사용)
        Event event = eventForm.of(account,study);
        return eventRepository.save(event);
    }
}
