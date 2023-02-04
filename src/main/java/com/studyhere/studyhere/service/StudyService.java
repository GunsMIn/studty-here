package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.dto.StudyDescriptionForm;
import com.studyhere.studyhere.domain.dto.StudyForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
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

    /**스터디 조회 + 매니저 유무**/
    public Study findStudyIfManager(Account account,String path) {
        Study findStudy = checkStudy(path);
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



}
