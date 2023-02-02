package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.dto.*;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.AccountTag;
import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.repository.TagRepository;
import com.studyhere.studyhere.service.AccountService;
import com.studyhere.studyhere.service.AccountTagService;
import com.studyhere.studyhere.service.TagService;
import com.studyhere.studyhere.validator.NicknameValidator;
import com.studyhere.studyhere.validator.PasswordValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SettingController {

    private final AccountService accountService;
    private final AccountTagService accountTagService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;
    private final TagService tagService;


    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        //account에 있는 데이터를 Profile에 채워주는 것
        model.addAttribute("profile", modelMapper.map(account, Profile.class));
        return "settings/profile";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile, BindingResult result, Model model,
                                RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/profile";
        }
        //주의 account는 detached 객체
        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:/settings/profile";
    }

    /**
     * 비밀번호 수정 화면이동
     **/
    @GetMapping("/settings/password")
    public String passwordUpdateForm(@CurrentUser Account account, PasswordForm passwordForm, Model model) {
        model.addAttribute(account);
        model.addAttribute(passwordForm);
        return "settings/password";
    }

    /**
     * Validator를 사용한 비밀번호 일치 유효성 검사
     **/
    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        //글로벌 오류 비밀번호 일치 검사
        webDataBinder.addValidators(new PasswordValidator());
    }

    /**
     * 비밀번호 수정
     **/
    @PostMapping("/settings/password")
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm,
                                 BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/password";
        }
        /*비밀번호 valid 통과시 수정 진행*/
        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "비밀번호 변경이 되었습니다.");
        return "redirect:/settings/password";
    }

    @GetMapping("/settings/notifications")
    public String updateNotificationsForm(@CurrentUser Account account, Model model) {
        model.addAttribute("acount", account);
        model.addAttribute("notifications", modelMapper.map(account, Notifications.class));
        return "settings/notifications";
    }

    /**
     * 알림설정 변경
     **/
    @PostMapping("/settings/notifications")
    public String updateNotifications(@CurrentUser Account account, @Valid Notifications notifications, BindingResult result
            , Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/notifications";
        }
        accountService.updateNotification(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정이 변경 되었습니다.");
        return "redirect:/settings/notifications";
    }

    /**
     * Validator를 사용한 비밀번호 일치 유효성 검사
     **/
    @InitBinder("nicknameForm")
    public void initNickNameBinder(WebDataBinder webDataBinder) {
        //글로벌 오류 비밀번호 일치 검사
        webDataBinder.addValidators(nicknameValidator);
    }

    /**
     * 닉네임 변경 화면이동
     **/
    @GetMapping("/settings/account")
    public String updateNicknameForm(@CurrentUser Account account, Model model) {
        model.addAttribute("nicknameForm", modelMapper.map(account, NicknameForm.class));
        return "settings/account";
    }

    /**
     * 닉네임 변경 진행
     **/
    @PostMapping("/settings/account")
    public String updateNickName(@CurrentUser Account account, @Valid NicknameForm nicknameForm, BindingResult result,
                                 Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/account";
        }
        accountService.updateNickname(account, nicknameForm);
        attributes.addFlashAttribute("message", "닉네임 변경을 성공적으로 완료했습니다.");
        return "redirect:/settings/account";
    }

    /**ManyToMany**/
/*    @GetMapping("/settings/tags")
    public String showTags(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(t -> t.getTitle()).collect(Collectors.toList()));
        return "settings/tags";
    }

 /*   @PostMapping("/settings/tags/add")
    @ResponseBody
    public ResponseEntity addTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        log.info("에이작스 요청 :{}",tagForm);
        //태그 제목이 없을 시에 save
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }*/
    /**ManyToMany**/


    /**태그 값 넣기**/
    @PostMapping("/settings/tags/add")
    @ResponseBody
    public ResponseEntity addTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        log.info("에이작스 요청 :{}",tagForm);
        accountService.addInterestOfMember(account, tagForm);
        return ResponseEntity.ok().build();
    }


    /**나의 관심 주제 보여주기
     *
     *
     * **/
    @GetMapping("/settings/tags")
    public String showTags(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        List<String> tags = accountService.getTags(account);
        model.addAttribute("tags", tags);
        return "settings/tags";
    }
}
