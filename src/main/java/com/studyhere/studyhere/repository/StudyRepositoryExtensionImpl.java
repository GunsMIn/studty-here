package com.studyhere.studyhere.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.studyhere.studyhere.domain.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

import static com.studyhere.studyhere.domain.entity.QStudy.*;


public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {




    // 상위클래스(QuerydslRepositorySupport)에 기본생성자가 없기 때문에 🔽 만들어주자
    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {

        JPQLQuery<Study> query = from(study).where(
                study.published.isTrue()
                        .and(keywordEqTitle(keyword))
                        .or(keywordEqTag(keyword))
                        .or(keywordEqZone(keyword)))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .leftJoin(study.members, QAccount.account).fetchJoin()
                .distinct();

        //QuerydslRepositorySupport에서 지원해주는 getQuerydsl()를 사용하자
        JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Study> fetchResults = pageableQuery.fetchResults();// fetchResults() 사용해야 페이징 정보 포함
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }


    private BooleanExpression keywordEqTitle(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return study.title.containsIgnoreCase(keyword);
        }
        return null;
    }

    private BooleanExpression keywordEqTag(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return study.tags.any().title.containsIgnoreCase(keyword);
        }
        return null;
    }

    private BooleanExpression keywordEqZone(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return study.zones.any().localNameOfCity.containsIgnoreCase(keyword);
        }
        return null;
    }

    @Override
    public List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                .and(study.closed.isFalse())
                .and(study.tags.any().in(tags))
                .and(study.zones.any().in(zones)))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .orderBy(study.publishedDateTime.desc())
                .distinct()
                .limit(9);
        return query.fetch();
    }
}
