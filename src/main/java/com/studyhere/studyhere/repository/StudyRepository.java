package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> , StudyRepositoryExtension{

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"members", "managers", "tags", "zones"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(attributePaths = {"managers"})
    Study findIfManagerByPath(String path);

    @EntityGraph(attributePaths = "members" )
    Study findStudyWithMembersByPath(String path);

    @EntityGraph(attributePaths = {"members", "managers"})
    Study findStudyWithManagersAndMemebersByPath(String path);

    Study findStudyOnlyByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones"})
    Study findStudyWithTagAndZoneById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"})
    Study findStudyWithManagersAndMemebersById(Long id);
}
