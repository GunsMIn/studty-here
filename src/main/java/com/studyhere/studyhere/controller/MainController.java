package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final NotificationRepository notificationRepository;

    /**메인화면**/
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }

        /**view를 렌더링 하기직전에 읽지 않은 알람 check - > interceptor로 리펙토링**/
       /* long count = notificationRepository.countByAccountAndChecked(account, false);
        model.addAttribute("hasNotification", count > 0);*/

        return "index";
    }


    /**로그인 화면이동**/
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
