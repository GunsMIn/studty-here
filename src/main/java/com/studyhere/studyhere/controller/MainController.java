package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.repository.AccountRepository;
import com.studyhere.studyhere.repository.EnrollmentRepository;
import com.studyhere.studyhere.repository.NotificationRepository;
import com.studyhere.studyhere.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudyRepository studyRepository;

    /**
     * 메인화면
     **/
    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        //로그인 한 회원 main화면
        if (account != null) {
            Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
          /*  List<Study> studyList = studyRepository.findByAccount(accountLoaded.getTags(), accountLoaded.getZones());*/
           
            model.addAttribute(accountLoaded);
            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded, true));
            model.addAttribute("studyList", studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));

            model.addAttribute("studyManagerOf",
                    studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            model.addAttribute("studyMemberOf",
                    studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
            return "index-after-login";
        }

        model.addAttribute("studyList", studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
        return "index";
    }

    /**
     * 로그인 화면이동
     **/
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/search/study")
    public String searchStudy(String keyword, Model model, @PageableDefault(size = 9, sort = "publishedDateTime",
            direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }

    @GetMapping("/main")
    public String goMain() {
        return "index";
    }
}
