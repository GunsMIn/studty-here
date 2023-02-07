package com.studyhere.studyhere.domain.events.eventListener;

import com.studyhere.studyhere.config.AppProperties;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Notification;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.entity.enumtype.NotificationType;
import com.studyhere.studyhere.domain.events.event.StudyCreatedEvent;
import com.studyhere.studyhere.domain.events.event.StudyUpdateEvent;
import com.studyhere.studyhere.domain.predicate.AccountPredicates;
import com.studyhere.studyhere.email.EmailMessage;
import com.studyhere.studyhere.email.EmailService;
import com.studyhere.studyhere.repository.AccountRepository;
import com.studyhere.studyhere.repository.NotificationRepository;
import com.studyhere.studyhere.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Transactional
@RequiredArgsConstructor
@Component
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    /**관심있는 주제와 지역의 스터디가 생성되었을시 이벤트 리스너**/
    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        //tags 정보와 지역정보를 참조할 수 있는 study
        Study study = studyRepository.findStudyWithTagAndZoneById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> accounts
                = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));

        for (Account account : accounts) {
            /**이메일 발송**/
            if (account.isStudyCreatedByEmail()) {
                sendStudyCreatedEmail(study, account, "새로운 스터디가 생겼습니다",
                        "스터디히어 : '" + study.getTitle() + "' 스터디가 생겼습니다.");
            }

            /**알림 생성**/
           if (account.isStudyCreatedByWeb()) {
               createNotification(study,account,study.getShortDescription(), NotificationType.STUDY_CREATED);
            }
        }

    }

    /** 스터디 모임 update 이벤트 리스너
     * 1. 스터디 정보 수정했을 경우
     * 2. 스터디 종료 했을 경우
     * 3. 해당 스터디의 모임을 열었을 경우
     * 3. 해당 스터디의 모임을 종료했을 경우
     * **/
    @EventListener
    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
        //해당 스터디의 id로 스터디와 멤버 매니저를 참조해서 가져와주자
        Study study = studyRepository.findStudyWithManagersAndMemebersById(studyUpdateEvent.getStudy().getId());
        //중복을 허용하지 않은 set구조의 변수로 study의 manager와 member를 넣어주자
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        accounts.forEach(account -> {
            if (account.isStudyUpdatedByEmail()) {
                sendStudyCreatedEmail(study, account, studyUpdateEvent.getMessage(),
                        "스터디히어, '" + study.getTitle() + "' 스터디에 새소식이 있습니다.");
            }

            if (account.isStudyUpdatedByWeb()) {
                createNotification(study, account, studyUpdateEvent.getMessage(), NotificationType.STUDY_UPDATED);
            }
        });
    }



    /**웹 알림 생성**/
    private void createNotification(Study study, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/" + study.encodePath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    /**이메일 알림 이메일 발송**/
    private void sendStudyCreatedEmail(Study study, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.encodePath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

}
