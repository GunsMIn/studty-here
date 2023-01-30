package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.domain.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MainController {

    /**메인화면**/
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        return "index";
    }


    /**로그인 화면이동**/
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
