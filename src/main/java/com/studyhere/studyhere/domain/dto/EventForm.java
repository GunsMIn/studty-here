package com.studyhere.studyhere.domain.dto;

import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Event;
import com.studyhere.studyhere.domain.entity.EventType;
import com.studyhere.studyhere.domain.entity.Study;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class EventForm {

    @NotBlank
    @Length(max = 50)
    private String title;

    private String description;
    /**기본값 : 선챡순**/
    private EventType eventType = EventType.FCFS;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    /**기본갑 :2명 최소 : 2명 **/
    @Min(2)
    private Integer limitOfEnrollments = 2;

    public Event of(Account account, Study study) {
        return Event.builder()
                .title(title)
                .description(description)
                .eventType(eventType)
                .createdDateTime(LocalDateTime.now())
                .endEnrollmentDateTime(endEnrollmentDateTime)
                .limitOfEnrollments(limitOfEnrollments)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .createdBy(account)
                .study(study)
                .build();

    }

}
