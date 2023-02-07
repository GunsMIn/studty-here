package com.studyhere.studyhere.service;


import com.studyhere.studyhere.domain.entity.Notification;
import com.studyhere.studyhere.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void markReadNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            notification.markAsRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public void markReadNotification(Notification notification) {
        notification.markAsRead(true);
        notificationRepository.save(notification);
    }
}
