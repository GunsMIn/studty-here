package com.studyhere.studyhere.validator;


import com.studyhere.studyhere.domain.dto.NicknameForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**닉네임 변경 시 사용가능한 닉네임인지 확인하는 validator **/
@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if (byNickname != null) {
            errors.rejectValue("nickname","wrong.value","이미 존재하는 닉네입입니다.");
        }
    }
}
