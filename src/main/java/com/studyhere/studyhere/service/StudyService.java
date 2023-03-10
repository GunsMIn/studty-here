package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.dto.StudyDescriptionForm;
import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.domain.entity.Zone;
import com.studyhere.studyhere.domain.events.event.StudyCreatedEvent;
import com.studyhere.studyhere.domain.events.event.StudyUpdateEvent;
import com.studyhere.studyhere.repository.StudyRepository;
import com.studyhere.studyhere.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static com.studyhere.studyhere.domain.dto.StudyForm.*;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TagRepository tagRepository;

    /**스터디 생성
     * 알림처리
     * **/
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
        Study study = checkStudy(path);
        //manager가 아닐 때 스터디에 대한 수정이 불가능하다.
        if (!account.isManager(study)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return study;
    }

    /**스터디 조회 + 매니저 fetch join**/
    public Study findStudyFetchManager(Account account,String path) {
        Study study = studyRepository.findIfManagerByPath(path);
        if (study == null) {
            throw new IllegalStateException("해당 스터디가 없습니다.");
        }
        //manager가 아닐 때 스터디에 대한 수정이 불가능하다.
        if (!account.isManager(study)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return study;
    }

    /**알림 이벤트 발생**/
    /**스터디 수정**/
    public void updateStudy(Study study, StudyDescriptionForm studyDescriptionForm) {
        study.updateDescription(studyDescriptionForm);
        eventPublisher.publishEvent(new StudyUpdateEvent(study,"스터디 소개를 수정했습니다."));
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

    /**알림 이벤트 발생**/
    /**스터디 오픈하기**/
    public void publish(Study study) {
        study.publishStudyState();
        eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }

    /**알림 이벤트 발생**/
    /**스터디에 오픈마감하기 + 스터디 변경 알림**/
    public void close(Study study) {
        study.closeStudyState();
        eventPublisher.publishEvent(new StudyUpdateEvent(study,"스터디가 종료됐습니다."));
    }

    /**알림 이벤트 발생**/
    /**스터디 인원 모집 시작**/
    public void enableRecruit(Study study) {
        study.enableRecruiting();
        eventPublisher.publishEvent(new StudyUpdateEvent(study,"팀원 모집을 시작합니다."));
    }

    /**알림 이벤트 발생**/
    /**스터디 인원 모집 마감**/
    public void stopRecruit(Study study) {
        study.disableRecruiting();
        eventPublisher.publishEvent(new StudyUpdateEvent(study,"팀원 모집을 중단합니다."));
    }

    /**1.스터디 경로 check -> 정규식에 맞는 study path인지 검사
     * 2.이미 존재하는 study path인지 검사
     * **/
    public boolean checkPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }else{
            if (studyRepository.findByPath(newPath) != null) {
                throw new RuntimeException("이미 존재하는 study path입니다.");
            }
            return true;
        }

    }
    /**스터디 path 수정**/
    public void changeStudyPath(Study study, String newPath) {
        study.changePath(newPath);
    }
    
    /**스터디 제목이 50글자 이하인 check하는 메서드**/
    public boolean checkChangeTitle(String newTitle) {
        return newTitle.length() <= 50;
    }
    /**스터디 제목 수정**/
    public void updateStudyTitle(Study study, String newTitle) {
        study.changeTitle(newTitle);
    }
    /**스터디 삭제
     * 공개중인 스터디는 삭제 불가능 (비공개 상태이면 삭제 가능)
     * **/
    public void remove(Study study) {
        if (study.isRemovable()) {
            studyRepository.delete(study);
        }else{
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    public void addMember(Study study, Account account) {
        study.addMembers(account);
    }

    public void removeMember(Study study, Account account) {
        study.removeMembers(account);
    }

    public Study getStudy(String path) {
        Study study = checkStudy(path);
        return study;
    }

    public Study findStudyByPath(String path) {
        Study study = studyRepository.findStudyOnlyByPath(path);
        if (study == null) {
            throw new IllegalStateException("해당 스터디를 찾을 수 없습니다");
        }
        return study;
    }





    public void generateTestData(Account account) {
        for (int i = 0; i < 30 ; i++) {
            String randomValue = RandomString.make(5);

            Study study = Study.builder()
                    .title("테스트 스터디" + randomValue)
                    .path("test" + randomValue)
                    .shortDescription("테스트용 스터디 입니다.")
                    .fullDescription("test fullDescription")
                    .tags(new HashSet<>())
                    .managers(new HashSet<>())
                    .build();

            study.publishStudyState();
            Study newStudy = createNewStudy(study, account);
            Tag jpa = tagRepository.findByTitle("JPA");
            newStudy.getTags().add(jpa);
        }
    }
}
