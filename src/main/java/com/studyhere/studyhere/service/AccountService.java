package com.studyhere.studyhere.service;

import com.studyhere.studyhere.controller.ConsoleMailSender;
import com.studyhere.studyhere.domain.dto.Profile;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ConsoleMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param signUpForm 회원가입 시 필요한 request
     *  save()와 sendSignUpEmail()메서드를 사용하여  회원가입 후 인증 이메일 발송
     * **/
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

    /**
     * 이메일 check 진행
     * 1. emailVerified 를 true로
     * 2. joinedAt 시간을 현재시산으로
     *
     * 자동로그인 진행
     * **/
    public void checkEmail(Account account) {
        account.completeSignUp();
        login(account);
    }


    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null){ // 이메일로 찾지 못한 경우 닉네임으로 찾는다.
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if(account == null){ // 닉네임으로도 찾지 못한다면 에러를 던짐
            throw new UsernameNotFoundException(emailOrNickname);
        }
        // Principal 에 해당하는 객체를 리턴한다.
        return new UserAccount(account);
    }

    /**프로필 변경**/
    public void updateProfile(Account account, Profile profile) {
        account.changeProfile(profile);
        /**주의! 현재 account는 detached객체라서 save()를 해줘야 변경이된다.**/
        accountRepository.save(account);
    }

    /**비밀번호 변경**/
    public void updatePassword(Account account, String newPassword) {
        /**현재 account는 detached객체**/
        account.changePassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account); //merge 진행
    }
}
