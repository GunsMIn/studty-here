package com.studyhere.studyhere.service;

import com.studyhere.studyhere.controller.ConsoleMailSender;
import com.studyhere.studyhere.domain.dto.SignUpForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.userdetail.UserAccount;
import com.studyhere.studyhere.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ConsoleMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param signUpForm 회원가입 시 필요한 request
     *  save()와 sendSignUpEmail()메서드를 사용하여  회원가입 후 인증 이메일 발송
     * **/
    @Transactional
    public Account processNewAccount(@Valid SignUpForm signUpForm) {
        //회원 저장
        Account newAccount = save(signUpForm);
        //저장 완료 시 이메일 체크에 필요한 토큰 발급🔽
        newAccount.generateEmailCheckToken(); /**newAccount라는 객체는 detached 상태이기 때문에 @Transactional을 붙여줘서 persist상태를 유지시켜준다.  **/
        sendSignUpEmail(newAccount);
        return newAccount;
    }

    /**회원 가입 save 메서드**/
    private Account save(@Valid SignUpForm signUpForm) {
        String encode = passwordEncoder.encode(signUpForm.getPassword());
        Account account = signUpForm.of(encode);
        return accountRepository.save(account);
    }

    /**이메일에 token보내는 메서드**/
    private void sendSignUpEmail(Account newAccount) {
        // TODO 이메일 보내기
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(newAccount.getEmail());
        mail.setSubject("스터디히어, 회원 가입 인증");
        mail.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                + "&email=" + newAccount.getEmail());
        mailSender.send(mail);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        log.info("로그인시 :{}",account.toString());
        SecurityContextHolder.getContext().setAuthentication(token);

    }

}
