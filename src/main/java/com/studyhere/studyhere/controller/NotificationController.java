package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Notification;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.entity.enumtype.NotificationType;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.repository.NotificationRepository;
import com.studyhere.studyhere.service.NotificationService;
import com.studyhere.studyhere.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

import static com.studyhere.studyhere.domain.entity.enumtype.NotificationType.*;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;
    private final NotificationService service;
    private final StudyService studyService;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentUser Account account, Model model) {
        /**아직 읽지 않은 알람 리스트**/
        List<Notification> notifications = repository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);
        /**읽은 알람 갯수**/
        long numberOfChecked = repository.countByAccountAndChecked(account, true);
        //매개변수 : model, 읽지 않은 알림 , 읽은 알림 갯수 , 읽지 않은 알림 갯수
        putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());
        model.addAttribute("isNew", true);
        //읽은 알림으로 처리🔽
        // TODO 추후 읽은 알림처리를 상세처리에서
        service.markReadNotifications(notifications);
        return "notification/list";
        }

  /*  *//**스터디 상세보기**//*
    @GetMapping("/study/{path}/read")
    public String viewStudy(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study findStudy = studyService.findStudy(path);
        studyService.checkStudy(path);
        model.addAttribute("study", findStudy);
        service.markReadNotification(notification);
        return "study/view";
    }
*/


    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = repository.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
        long numberOfNotChecked = repository.countByAccountAndChecked(account, false);
        putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);
        model.addAttribute("isNew", false);
        return "notification/list";
    }


    @PostMapping("/notifications/delete")
    public String deleteNotifications(@CurrentUser Account account) {
        repository.deleteByAccountAndChecked(account, true);
        return "redirect:/notifications";
    }


    /**읽지 않은 알림들을 알림 타입에 따라서 나눠주는 메서드**/
    private void putCategorizedNotifications(Model model, List<Notification> notifications,
                                             long numberOfChecked, long numberOfNotChecked) {
        //새로운 스터디 알람 리스트(관심주제,지역)
        List<Notification> newStudyNotifications = new ArrayList<>();
        //모임 등록 알람 리스트
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        //참여 스터디 알람 리스트
        List<Notification> watchingStudyNotifications = new ArrayList<>();

        for (Notification notification : notifications) {
            if (notification.getNotificationType().equals(STUDY_CREATED)) {
                newStudyNotifications.add(notification);
            } else if (notification.getNotificationType().equals(EVENT_ENROLLMENT)) {
                eventEnrollmentNotifications.add(notification);
            } else if (notification.getNotificationType().equals(STUDY_UPDATED)) {
                watchingStudyNotifications.add(notification);
            } else {
                throw new IllegalStateException("해당 알람 타입은 존재하지 않습니다");
            }
        }

        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);

    }
}
