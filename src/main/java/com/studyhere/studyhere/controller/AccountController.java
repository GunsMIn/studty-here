package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.dto.SignUpForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.repository.AccountRepository;
import com.studyhere.studyhere.repository.StudyRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final StudyRepository studyRepository;
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
        return "redirect:/settings/tags";
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
        accountService.checkEmail(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    /**나의 프로필 페이지 가기**/
    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, @CurrentUser Account account, Model model) {
        Account byNickname = accountRepository.findByNickname(nickname);
        if (nickname == null) {
            throw new IllegalStateException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        model.addAttribute("studyManagerOf",
                studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
        model.addAttribute("studyMemberOf",
                studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
        // Owner인지 확인하는 flag
        boolean isOwner = byNickname.equals(account);
        model.addAttribute("account",byNickname);
        model.addAttribute("isOwner", isOwner);
        return "account/profile";
    }

    /**비밀번호 일어버렸을 때 페이지**/
    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    /**비밀번호 분실 후 이메일 로그인 진행 **/
    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model , RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "유효한 이메일 주소가 아닙니다.");
            return "account/email-login";
        }
       /* if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "이메일 로그인은 1시간 뒤에 사용할 수 있습니다.");
            return "account/email-login";
        }*/
        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "이메일을 인증 메일을 발송했습니다");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";
        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }

        accountService.login(account);
        return view;
    }
}
