package com.studyhere.studyhere.repository;


import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.AccountTag;
import com.studyhere.studyhere.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AccountTagRepository extends JpaRepository<AccountTag,Long> {

    List<AccountTag> findByAccount(Account account);
}

