package com.studyhere.studyhere.repository;

import com.studyhere.studyhere.domain.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone,Long> {

    Zone findByCityAndProvince(String city, String province);
}
