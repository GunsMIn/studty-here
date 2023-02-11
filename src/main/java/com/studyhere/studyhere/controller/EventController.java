package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.dto.EventForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Enrollment;
import com.studyhere.studyhere.domain.entity.Event;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.repository.EventRepository;
import com.studyhere.studyhere.repository.StudyRepository;
import com.studyhere.studyhere.service.EventService;
import com.studyhere.studyhere.service.StudyService;
import com.studyhere.studyhere.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor @Slf4j
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;




    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        //글로벌 오류 (1.모임 등록 날짜 2.모임 시작 날짜 에러)
        webDataBinder.addValidators(new EventValidator());
    }

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
        log.info(eventForm.toString());
        //모임을 만들기 위해서는 study와 manager 참조만으로 가능 @EntityGraphe
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
        Study study = studyService.findStudy(path);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(study);
        return "event/view";
    }

    /**해당 스터디의 event(모임) 리스트 보기**/
    @GetMapping("/events")
    public String viewStudyEvents(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        List<Event> eventList = eventRepository.findByStudy(study);

        /**진행중인 이벤트 / 끝난 이벤트**/
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        /**해당 모임의 종료시간이 지금보다 뒤라면 아직 진행중인 모임(새모임)**/
        eventList.forEach(e->{
            if (e.getEndDateTime().isAfter(LocalDateTime.now())) {
                newEvents.add(e);
            }else{
                oldEvents.add(e);
            }
        });

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "study/events";
    }


    @GetMapping("/events/{id}/edit")
    public String editEventForm(@CurrentUser Account account,@PathVariable String path,@PathVariable("id") Event event,Model model) {
        Study study = studyService.findStudyFetchManager(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String editEvents(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event,
                                             @Valid EventForm eventForm,BindingResult result,Model model) {
        Study study = studyService.findStudyIfManager(account, path);
        /*수정한 제한 인원이 확정된 인원보다 적을 경우 에러 처리*/
        if (eventForm.getLimitOfEnrollments() < event.confirmNumber()) {
            result.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참가 신청보다 모집 인원 수가 커야 합니다.");
        }
        if (result.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/update-form";
        }
        Event findEvent = eventService.findEvent(event);
        eventService.change(findEvent, eventForm);
        return "redirect:/study/" + study.encodePath() +  "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Study study = studyService.findStudyIfManager(account, path);
        Event findEvent = eventService.findEvent(event);
        eventService.delete(findEvent);
        log.info("삭제 완료!");
        return "redirect:/study/" + study.encodePath() + "/events";
    }
    /**모임 신청**/
    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentUser Account account,@PathVariable String path,@PathVariable("id") Event event) {
        Study study = studyService.findStudyByPath(path);
        eventService.createEnrollment(account, event);
        return "redirect:/study/" + study.encodePath() + "/events/" + event.getId();
    }

    /**모임 신청 취소**/
    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("id") Event event) {
        Study study = studyService.findStudyByPath(path);
        eventService.cancelEnrollment(account, event);
        return "redirect:/study/" + study.encodePath() + "/events/" + event.getId();
    }

    @GetMapping("events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.findStudyIfManager(account, path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/study/" + study.encodePath() + "/events/" + event.getId();
    }
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentUser Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.findStudyIfManager(account, path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/study/" + study.encodePath() + "/events/" + event.getId();
    }


    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentUser Account account, @PathVariable String path,
                                    @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.findStudyIfManager(account, path);
        eventService.checkInEnrollment(enrollment);
        return "redirect:/study/" + study.encodePath() + "/events/" + event.getId();
    }

    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelCheckInEnrollment(@CurrentUser Account account, @PathVariable String path,
                                          @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
        Study study = studyService.findStudyIfManager(account, path);
        eventService.cancelCheckInEnrollment(enrollment);
        return "redirect:/study/" + study.encodePath() + "/events/" + event.getId();
    }


}
