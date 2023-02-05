package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.dto.StudyDescriptionForm;
import com.studyhere.studyhere.domain.dto.StudyForm;
import com.studyhere.studyhere.domain.dto.TagForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.domain.entity.Zone;
import com.studyhere.studyhere.repository.AccountRepository;
import com.studyhere.studyhere.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;

    /**스터디 생성**/
    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    /**스터디 조회**/
    public Study findStudy(String path) {
        return studyRepository.findByPath(path);
    }

    /**스터디 조회 + 매니저 유무(매니저일때 study반환)**/
    public Study findStudyIfManager(Account account,String path) {
        Study findStudy = checkStudy(path);
        //manager가 아닐 때 스터디에 대한 수정이 불가능하다.
        if (!account.isManager(findStudy)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return findStudy;
    }

    /**스터디 조회 + 매니저 fetch join**/
    public Study findStudyFetchManager(Account account,String path) {
        Study findStudy = studyRepository.findIfManagerByPath(path);
        if (findStudy == null) {
            throw new IllegalStateException("해당 스터디가 없습니다.");
        }
        //manager가 아닐 때 스터디에 대한 수정이 불가능하다.
        if (!account.isManager(findStudy)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return findStudy;
    }


    /**스터디 수정**/
    public void updateStudy(Study study, StudyDescriptionForm studyDescriptionForm) {
        study.updateDescription(studyDescriptionForm);
    }

    /**스터디 존재 여부 check메서드**/
    public Study checkStudy(String path) {
        Study study = studyRepository.findByPath(path);
        if (study ==null) {
            throw new IllegalStateException("해당 스터디가 없습니다.");
        }
        return study;
    }

    /**스터디 배너 사용 가능하게 변경**/
    public Study enableBanner(Account account, String path) {
        Study study = findStudyIfManager(account, path);
        //배너 사용으로 변경
        study.changeEnableBanner();
        return study;
    }
    /**스터디 배너 사용 불가능하게 변경**/
    public Study disableBanner(Account account, String path) {
        Study study = findStudyIfManager(account, path);
        //배너 사용으로 변경
        study.changeDisableBanner();
        return study;
    }
    /**스터디 배너 이미지 변경**/
    public void changeBanner(Study study, String image) {
        study.changeBannerImage(image);
    }
    /**스터디에 tag(관심주제) 추가하기**/
    public void addTagOfStudy(Study study,Tag tag) {
        study.getTags().add(tag);
    }
    /**스터디에 tag(관심주제) 삭제하기**/
    public void removeTagOfStudy(Study study, Tag tag) {
        study.getTags().remove(tag);
    }
    /**스터디에 zone(지역) 추가하기**/
    public void addZoneOfStudy(Study study, Zone zone) {
        study.getZones().add(zone);
    }
    /**스터디에 zone(지역) 삭제하기**/
    public void removeZoneOfStudy(Study study, Zone zone) {
        study.getZones().remove(zone);
    }
    /**스터디에 오픈하기**/
    public void publish(Study study) {
        study.publishStudyState();
    }
    /**스터디에 오픈마감하기**/
    public void close(Study study) {
        study.closeStudyState();
    }

    public void enableRecruit(Study study) {
        study.enableRecruiting();
    }
}
