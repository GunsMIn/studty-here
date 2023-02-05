package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.dto.EventForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Event;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.repository.StudyRepository;
import com.studyhere.studyhere.service.EventService;
import com.studyhere.studyhere.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;


    @GetMapping("/new-event")
    public String createEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.findStudyFetchManager(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String createEvent(@CurrentUser Account account, @PathVariable String path, @Valid EventForm eventForm ,
                                                        BindingResult result, Model model) {
        Study study = studyService.findStudyFetchManager(account, path);
        if (result.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }
        Event event = eventService.createEvent(study, eventForm, account);
        return "redirect:/study/" + study.encodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event,
                           Model model) {
        Study study = studyService.findStudyFetchManager(account, path);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(study);
        return "event/view";
    }

}
