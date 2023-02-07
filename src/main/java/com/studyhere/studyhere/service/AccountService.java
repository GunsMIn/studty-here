package com.studyhere.studyhere.service;

import com.studyhere.studyhere.config.AppProperties;
import com.studyhere.studyhere.domain.dto.*;
import com.studyhere.studyhere.domain.entity.Account;


import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.domain.entity.Zone;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.domain.userdetail.UserAccount;
import com.studyhere.studyhere.email.EmailMessage;
import com.studyhere.studyhere.email.EmailService;
import com.studyhere.studyhere.repository.AccountRepository;

import com.studyhere.studyhere.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ZoneRepository zoneRepository;
    private final TagService tagService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    /**
     * @param signUpForm 회원가입 시 필요한 request
     *                   save()와 sendSignUpEmail()메서드를 사용하여  회원가입 후 인증 이메일 발송
     **/
    public Account processNewAccount(@Valid SignUpForm signUpForm) {
        //회원 저장
        Account newAccount = save(signUpForm);
        //저장 완료 시 이메일 체크에 필요한 토큰 발급🔽
        newAccount.generateEmailCheckToken(); /**newAccount라는 객체는 detached 상태이기 때문에 @Transactional을 붙여줘서 persist상태를 유지시켜준다.  **/
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    /**
     * 회원 가입 save 메서드
     **/
    private Account save(@Valid SignUpForm signUpForm) {
        String encode = passwordEncoder.encode(signUpForm.getPassword());
        Account account = signUpForm.of(encode);
        return accountRepository.save(account);
    }

    /**
     * 이메일에 token보내는 메서드
     **/
    public void sendSignUpConfirmEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디히어 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("스터디히어, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    /**
     * 로그인
     * NickName을 Principal로 넣어주는 로그인 메서드
     **/
    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    /**
     * 이메일 check 진행
     * 1. emailVerified 를 true로
     * 2. joinedAt 시간을 현재시산으로
     * <p>
     * 자동로그인 진행
     **/
    public void checkEmail(Account account) {
        account.completeSignUp();
        login(account);
    }


    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) { // 이메일로 찾지 못한 경우 닉네임으로 찾는다.
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if (account == null) { // 닉네임으로도 찾지 못한다면 에러를 던짐
            throw new UsernameNotFoundException(emailOrNickname);
        }
        // Principal 에 해당하는 객체를 리턴한다.
        return new UserAccount(account);
    }

    /**
     * 프로필 변경
     **/
    public void updateProfile(Account account, Profile profile) {
        account.changeProfile(profile);
        //ModelMapper 사용
        /*modelMapper.map(profile, account);*/
        /**주의! 현재 account는 detached객체라서 save()를 해줘야 변경이된다.**/
        accountRepository.save(account);
    }

    /**
     * 비밀번호 변경
     **/
    public void updatePassword(Account account, String newPassword) {
        /**현재 account는 detached객체**/
        account.changePassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account); //merge 진행
    }

    /**
     * 알림 설정 변경
     **/
    public void updateNotification(Account account, Notifications notifications) {
        account.changeNotifiacation(notifications);
        accountRepository.save(account);
    }

    /**
     * 닉네임 변경
     * <p>
     * 닉네임 변경시 login()을 다시 해줘야 한다.
     * 네비바 authentication 갱신 목적
     **/
    public void updateNickname(Account account, NicknameForm nicknameForm) {
        account.changeNickname(nicknameForm.getNickname());
        accountRepository.save(account);
        login(account);
    }

    /**
     * 비밀번호 분실 시 로그인 링크 보내기
     **/
    public void sendLoginLink(Account account) {
       /* //이메일 인증 토큰 생성
        account.generateEmailCheckToken();
        //이메일 토큰과 이메일 목표 url로 발송
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(account.getEmail());
        mail.setSubject("스터디히어, 로그인 링크");
        mail.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        mailSender.send(mail);*/
    }


  /*  public void addInterestOfMember(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        //1.관심주제 제목으로 관심주제 생성(이미 존재하면 값 반환)
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        //2.회원(account)과 관심주제(tag)를 accountTag(회원관심주제)에 저장
        AccountTag accountTag = AccountTag.createAccountTag(account, tag);
        accountTagRepository.save(accountTag);
    }*/



    /**
     * 1. accountTag 에서 account로 해당 accountTag  조회 ->  (List<AccountTag>)
     * 2. 회원의 태그 저장소(tagStore)에 해당 accountTag의 제목을 add
     * 3. 회원의 태그 반환
     * **/
    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }


    /**해당 회원의 태그 지우기**/
    public void removeTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }
    /**회원의 지역 리스트 반환**/
    public Set<Zone> getZones(Account account) {
        Optional<Account> optionalAccount = accountRepository.findById(account.getId());
        Account owner = optionalAccount.orElseThrow(() -> new IllegalStateException("해당 회원은 존재하지 않습니다"));
        return owner.getZones();
    }

    /**해당 회원의 지역 추가**/
    public void addZone(Account account, Zone zone) {
        Account owner = accountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalStateException("해당 회원은 존재하지 않습니다."));
        //해당 회원의 지역에 save(),add()
        owner.getZones().add(zone);
    }

    /**해당 회원의 지역정보 삭제**/
    public void deleteZone(Account account, Zone zone) {
        Account owner =
                accountRepository.findById(account.getId()).orElseThrow(() -> new IllegalStateException("해당 회원은 존재하지 않습니다"));
        //해당 회원의 지역에 delete(),remove()
        owner.getZones().remove(zone);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }



    /**
     * 해당회원 조회 후 tag(관심 목록)추가
     **/
 /*   public void addTag(Account account, Tag tag) {
        Optional<Account> optionalAccount = accountRepository.findById(account.getId());
        optionalAccount.ifPresent(a -> a.getTags().add(tag));
    }*/

  /*  public Set<Tag> getTags(Account account) {
        Optional<Account> optionalAccount = accountRepository.findById(account.getId());
        Set<Tag> tags = optionalAccount.orElseThrow().getTags();
        return tags;
    }*/
}