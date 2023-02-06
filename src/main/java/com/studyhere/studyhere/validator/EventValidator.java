package com.studyhere.studyhere.validator;

import com.studyhere.studyhere.domain.dto.EventForm;
import com.studyhere.studyhere.domain.dto.NicknameForm;
import com.studyhere.studyhere.domain.entity.Event;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return EventForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;
        /**접수 마감날짜가 지금보다 이전이면 error**/
        if (isNotValidEnrollmentDateTime(eventForm)) {
            errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "모임 접수 종료 일시를 정확히 입력하세요.");
        }
        /**모임 종료가 모임 시작날짜보다 전일 때 || 모임 종료가 접수 마감 날짜 보다 전일때 error**/
        if (isNotValidEndDateTime(eventForm)) {
            errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
        }
        /**모임 시작일자가 접수 마감일자 보다 빠를 때 error**/
        if (isNotValidStartDateTime(eventForm)) {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력하세요.");
        }
    }


    /**수정한 제한 인원이 확정된 인원보다 적을 경우 에러 처리**/
    public void validateUpdateForm(EventForm eventForm, Event event, Errors errors) {
        if (eventForm.getLimitOfEnrollments() < event.confirmNumber()) {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참가 신청보다 모집 인원 수가 커야 합니다.");
        }
    }

    private boolean isNotValidEnrollmentDateTime(EventForm eventForm) {
        return eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }


    private boolean isNotValidEndDateTime(EventForm eventForm) {
        return eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime())||
            eventForm.getEndDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }

    private boolean isNotValidStartDateTime(EventForm eventForm) {
        return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
    }

}
