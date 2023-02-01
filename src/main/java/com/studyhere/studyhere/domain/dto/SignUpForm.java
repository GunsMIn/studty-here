package com.studyhere.studyhere.domain.dto;

import com.studyhere.studyhere.domain.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpForm {

    @NotBlank
    private String nickname;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 4,max = 30)
    private String password;

    public Account of(String encodePassword) {
        return Account.builder()
                .nickname(nickname)
                .email(email)
                .password(encodePassword) // 암호화 한 비밀번호 저장
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .studyCreatedByWeb(true)
                .build();
    }
}
