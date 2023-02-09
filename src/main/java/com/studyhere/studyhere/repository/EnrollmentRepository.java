package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Enrollment;
import com.studyhere.studyhere.domain.entity.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
    boolean existsByAccountAndEvent(Account account, Event event);
    Enrollment findByAccountAndEvent(Account account, Event event);

    List<Enrollment> findByAccountAndAcceptedOrderByEnrolledAtDesc(Account account, boolean accepted);

}
