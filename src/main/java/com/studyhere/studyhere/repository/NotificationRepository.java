package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    /**알림 check**/
    long countByAccountAndChecked(Account account, boolean checked);

    /**회원의 아직 읽지 않은 알람 리스트 조회**/
    List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account account, boolean checked);
}
