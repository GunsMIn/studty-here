package com.studyhere.studyhere.domain.predicate;

import com.querydsl.core.types.Predicate;
import com.studyhere.studyhere.domain.entity.QAccount;
import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.domain.entity.Zone;

import java.util.List;
import java.util.Set;


public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags , Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
