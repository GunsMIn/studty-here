package com.studyhere.studyhere.validator;

import com.studyhere.studyhere.domain.dto.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**비밀번호 검증**/
public class PasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword","wrong.value","입력한 패스워드가 일치하지 않습니다.");
        }
    }
}
