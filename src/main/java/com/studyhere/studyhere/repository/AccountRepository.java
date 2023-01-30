package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface AccountRepository extends JpaRepository<Account,Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

   Account findByEmail(String email);
}
