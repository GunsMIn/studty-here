package com.studyhere.studyhere.controller;

import com.studyhere.studyhere.domain.dto.StudyDescriptionForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;

    /**
     * 스터디 수정 기능
     * 1. account가 manager 인지 확인
     * 2. ModelMapper 를 사용한 기존 스터디 정보 전달
     **/
    @GetMapping("/description")
    public String viewStudySetting(@CurrentUser Account account, @PathVariable String path, StudyDescriptionForm studyDescriptionForm, Model model) {
        Study study = studyService.findStudyIfManager(account, path);
        model.addAttribute("study", study);
        model.addAttribute("studyDescriptionForm", modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }
    /**스터디 수정
     * 1. account가 manager 인지 확인
     * 2. 변경감지 수정
     * 3. study path 한글 대비 -> encode()
     * **/
    @PostMapping("/description")
    public String updateStudyDescription(@CurrentUser Account account, @PathVariable String path
                                , @Valid StudyDescriptionForm studyDescriptionForm, BindingResult result ,
                                         Model model , RedirectAttributes attributes) {
        //스터디 조회
        Study study = studyService.findStudyIfManager(account, path);
        if (result.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/description";
        }
        //스터디 수정 🔽
        studyService.updateStudy(study, studyDescriptionForm);
        attributes.addFlashAttribute("message", "스터디가 수정됐습니다.");
        return "redirect:/study/"+study.encodePath()+"/settings/description";
    }
}
