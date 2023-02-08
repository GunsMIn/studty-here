package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Study;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {
    List<Study> findByKeyword(String keyword);
}
