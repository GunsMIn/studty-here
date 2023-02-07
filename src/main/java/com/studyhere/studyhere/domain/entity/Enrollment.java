package com.studyhere.studyhere.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDateTime enrolledAt;

    /**확정**/
    private boolean accepted;

    /**참석 여부**/
    private boolean attended;



    public void changeAccepted(boolean value) {
        this.accepted = value;
    }

    public void changeAttended(boolean value) {
        this.attended = value;
    }

}
