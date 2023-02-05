package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.dto.StudyForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.repository.StudyRepository;
import com.studyhere.studyhere.service.StudyService;
import com.studyhere.studyhere.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final StudyFormValidator studyFormValidator;

    /***study Path 검사(path는 unique한 값이어야한다.)*/
    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    /**스터디 개설 페이지 이동**/
    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new StudyForm());
        return "study/form";
    }

    /**스터디 생성**/
    @PostMapping("/new-study")
    public String newStudySubmit(@CurrentUser Account account, @Valid StudyForm studyForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "study/form";
        }
        Study study = modelMapper.map(studyForm, Study.class);
        Study newStudy = studyService.createNewStudy(study, account);
        return "redirect:/study/" + URLEncoder.encode(newStudy.getPath(),StandardCharsets.UTF_8);
    }

    /**스터디 상세보기**/
    @GetMapping("/study/{path}")
    public String viewStudy(@CurrentUser Account account, @PathVariable String path,Model model) {
        Study findStudy = studyService.findStudy(path);
        studyService.checkStudy(path);
        model.addAttribute("study", findStudy);
        return "study/view";
    }

    /**스터디 참여인 보기 페이지 이동**/
    @GetMapping("/study/{path}/members")
    public String viewStudyMembers(@CurrentUser Account account,@PathVariable String path,Model model) {
        Study study = studyService.checkStudy(path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/members";
    }


}
