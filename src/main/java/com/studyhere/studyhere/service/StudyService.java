package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.dto.StudyForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.repository.AccountRepository;
import com.studyhere.studyhere.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository repository;


    public Study createNewStudy(Study study, Account account) {
        Study newStudy = repository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    /*public Study openStudy(Account account, StudyForm studyForm) {
        log.info(studyForm.toString());
        Account manager = accountRepository.findById(account.getId()).orElseThrow(() -> new IllegalStateException("해당 회원은 존재하지 않습니다"));
        Study study = Study.builder()
                .path(studyForm.getPath())
                .title(studyForm.getTitle())
                .shortDescription(studyForm.getShortDescription())
                .fullDescription(studyForm.getFullDescription()).build();
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(manager);
        return study;
    }*/
}
