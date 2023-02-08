package com.studyhere.studyhere.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyhere.studyhere.domain.entity.QStudy;
import com.studyhere.studyhere.domain.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.studyhere.studyhere.domain.entity.QStudy.*;


public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {




    // 상위클래스(QuerydslRepositorySupport)에 기본생성자가 없기 때문에 🔽 만들어주자
    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public List<Study> findByKeyword(String keyword) {

        JPQLQuery<Study> query = from(study).where(
                study.published.isTrue()
                        .and(keywordEqTitle(keyword))
                        .or(keywordEqTag(keyword))
                        .or(keywordEqZone(keyword))
        );
        return query.fetch();
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
}
