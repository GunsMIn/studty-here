package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.dto.SignUpForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.repository.AccountRepository;
import com.studyhere.studyhere.service.AccountService;
import com.studyhere.studyhere.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final SignUpFormValidator signUpFormValidator;




    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        //글로벌 오류 (1.이메일 중복, 2.닉네임 중복)
        webDataBinder.addValidators(signUpFormValidator);
    }
    /**회원가입 페이지 이동**/
    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    /**회원 가입 진행
     * 1. 회원가입 user정보 저장
     * 2. 이메일 check token 발금
     * 3. 이메일 발송
     * 4. 자동 로그인 설정
     * **/
    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, BindingResult result) {
        if (result.hasErrors()) {
            return "account/sign-up";
        }
        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    /**유효한 이메일인지 check
     * 1. 이메일 check token
     * 2. 이메일
     * 3. 자동 로그인
     * **/
    @GetMapping("/check-email-token")
    public String checkEmailToken(String token,String email,Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";
        //1.email이 존재하는지
        //2.emailCheckToken이 일치하는지
        if (account == null) {
            model.addAttribute("error", "Email이 틀렸습니다");
            return view;
        }
        if (!account.isValidToken(token)) {
            model.addAttribute("error", "Email Token 정보가 틀렸습니다.");
            return view;
        }
        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }



}
