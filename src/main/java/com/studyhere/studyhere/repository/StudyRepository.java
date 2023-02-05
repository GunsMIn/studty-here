package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface StudyRepository  extends JpaRepository<Study,Long> {
    boolean existsByPath(String path);

    @EntityGraph(attributePaths={"members","managers","tags","zones"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

}