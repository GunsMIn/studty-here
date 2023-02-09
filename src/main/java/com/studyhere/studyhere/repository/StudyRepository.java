package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.domain.entity.Zone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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





    List<Study> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Study> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);


}
