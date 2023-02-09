package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.domain.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {
    Page<Study> findByKeyword(String keyword, Pageable pageable);

    List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
