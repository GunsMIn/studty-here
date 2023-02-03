package com.studyhere.studyhere.domain.dto;

import com.studyhere.studyhere.domain.entity.Zone;
import lombok.Data;

@Data
public class ZoneForm {
    /**Asan(아산시)/South chungcheong**/

    private String zoneName;

    public String getCity() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public String getProvince() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public Zone of() {
        return Zone.builder()
                .city(getCity())
                .localNameOfCity(getLocalNameOfCity())
                .province(getProvince())
                .build();
    }

}

