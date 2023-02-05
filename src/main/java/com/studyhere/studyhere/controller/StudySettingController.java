package com.studyhere.studyhere.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyhere.studyhere.domain.dto.StudyDescriptionForm;
import com.studyhere.studyhere.domain.dto.TagForm;
import com.studyhere.studyhere.domain.dto.ZoneForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.domain.entity.Zone;
import com.studyhere.studyhere.domain.userdetail.CurrentUser;
import com.studyhere.studyhere.repository.TagRepository;
import com.studyhere.studyhere.repository.ZoneRepository;
import com.studyhere.studyhere.service.StudyService;
import com.studyhere.studyhere.service.TagService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingController {

    private final StudyService studyService;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;


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

    /**스터디 배너 이미지 생성 페이지로 이동**/
    @GetMapping("/banner")
    public String studyBannerForm(@CurrentUser Account account,@PathVariable String path, Model model) {
        Study study = studyService.findStudyIfManager(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/banner";
    }

    /**스터디 배너 이미지 수정**/
    @PostMapping("/banner")
    public String changeBanner(@CurrentUser Account account,@PathVariable String path,String image,RedirectAttributes attributes) {
        Study study = studyService.findStudyIfManager(account, path);
        studyService.changeBanner(study,image);
        attributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");
        return "redirect:/study/" + study.encodePath() + "/settings/banner";
    }
    /**스터디 배너 사용 여부 -> 사용 **/
    @PostMapping("/banner/enable")
    public String enableBanner(@CurrentUser Account account,@PathVariable String path) {
        Study study = studyService.enableBanner(account, path);
        return "redirect:/study/" + study.encodePath() + "/settings/banner";
    }
    /**스터디 배너 사용 여부 -> 미사용 **/
    @PostMapping("/banner/disable")
    public String disableBanner(@CurrentUser Account account,@PathVariable String path) {
        Study study = studyService.disableBanner(account, path);
        return "redirect:/study/" + study.encodePath() + "/settings/banner";
    }

    /**스터디 태그 등록 페이지 이동**/
    @GetMapping("/tags")
    public String studyTagsForm(@CurrentUser Account account,@PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.findStudyIfManager(account, path);
         //데이터베이스에 저장되어있는 관심주제를 list로 가져와서 자동완성 기능 view에 보냄
        List<String> whitelist = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("study",study);
        model.addAttribute("account",account);
        model.addAttribute("tags", study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        model.addAttribute("whitelist",objectMapper.writeValueAsString(whitelist));
        return "study/settings/tags";
    }

    /**스터디 관심주제(Tag) 등록**/
    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account,@PathVariable String path
                                    ,@RequestBody TagForm tagForm) {
        Study study = studyService.findStudyIfManager(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTagOfStudy(study,tag);
        return ResponseEntity.ok().build();
    }
    /**스터디 관심주제(Tag) 삭제**/
    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account,@PathVariable String path
                                         ,@RequestBody TagForm tagForm) {
        Study study = studyService.findStudyIfManager(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTagOfStudy(study, tag);
        return ResponseEntity.ok().build();
    }
    /**스터디 지역 선택 페이지 이동**/
    @GetMapping("/zones")
    public String studyZoneForm(@CurrentUser Account account,@PathVariable String path,Model model) throws JsonProcessingException {
        //1.스터디 조회 2.지역 자동 완성 list
        Study study = studyService.findStudyIfManager(account, path);
        List<String> whitelist = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        //view단으로 전달🔽
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("zones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
        return "study/settings/zones";
    }

    /**스터디 지역 추가**/
    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Study study = studyService.findStudyIfManager(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCity(), zoneForm.getProvince());
        studyService.addZoneOfStudy(study, zone);
        return ResponseEntity.ok().build();
    }
    /**스터디 지역 삭제제**/
    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Study study = studyService.findStudyIfManager(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCity(), zoneForm.getProvince());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeZoneOfStudy(study, zone);
        return ResponseEntity.ok().build();
    }


    /**
     * 스터디 설정 변경 페이지 이동
     **/
    @GetMapping("/study")
    public String studySettingForm(@CurrentUser Account account, @PathVariable String path,Model model) {
        Study study = studyService.findStudyIfManager(account, path);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        return "study/settings/study";
    }

    /**스터디 공개로 변경 (publish)**/
    @PostMapping("/study/publish")
    public String publishStudy(@CurrentUser Account account,@PathVariable String path,RedirectAttributes attributes) {
        Study study = studyService.findStudyFetchManager(account, path);
        studyService.publish(study);
        attributes.addFlashAttribute("message", "스터디를 공개했습니다.");
        return "redirect:/study/" + study.encodePath() + "/settings/study";
    }

    /**스터디 비공개로 변경 (스터디 close)**/
    @PostMapping("/study/close")
    public String closetStudy(@CurrentUser Account account,@PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.findStudyFetchManager(account, path);
        studyService.close(study);
        attributes.addFlashAttribute("message", "스터디를 종료했습니다.");
        return "redirect:/study/" + study.encodePath() + "/settings/study";
    }

    /**스터디 팀원 모집 시작**/
    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentUser Account account, @PathVariable String path,Model model,RedirectAttributes attributes) {
        Study study = studyService.findStudyFetchManager(account, path);
        if (!study.checkRecruitCondition()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/study/" + study.encodePath() + "/settings/study";
        }

        studyService.enableRecruit(study);
        attributes.addFlashAttribute("message", "인원모집을 시작합니다");
        return "redirect:/study/" + study.encodePath() + "/settings/study";
    }

    /****/
}
