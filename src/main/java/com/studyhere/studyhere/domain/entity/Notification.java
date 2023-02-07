package com.studyhere.studyhere.domain.entity;

import com.studyhere.studyhere.domain.entity.enumtype.NotificationType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.EnumType.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Account account;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    private LocalDateTime createdDateTime;

    @Enumerated(value = STRING)
    private NotificationType notificationType;


    public void markAsRead(boolean value) {
        this.checked = value;
    }

}