package com.studyhere.studyhere.domain.events;

import com.studyhere.studyhere.config.AppProperties;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Notification;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.entity.enumtype.NotificationType;
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
            /**알림 생성성**/
           if (account.isStudyCreatedByWeb()) {
               createNotification(study,account,study.getShortDescription(), NotificationType.STUDY_CREATED);
            }
        }

    }

    /**알림 생성**/
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

    /**관심주제 지역 스터디 생성 시 이메일 발송**/
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
