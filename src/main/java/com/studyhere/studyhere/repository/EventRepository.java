package com.studyhere.studyhere.repository;


import com.studyhere.studyhere.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Long> {
}
