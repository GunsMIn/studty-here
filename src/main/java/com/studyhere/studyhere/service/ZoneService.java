package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.entity.Zone;
import com.studyhere.studyhere.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository zoneRepository;

    /**ZoneService가 bean으로 등록된 다음 바로 실행**/
    @PostConstruct
    public void initZoneData() throws IOException {
        //지역 리소스가 하나도 없을 때
        if (zoneRepository.count() == 0) {
            //1.zones_kr.csv를 읽어온 다음
            //2.각 값을 ","를 split해주고
            //3.stream을 사용하여 Zone객체로 변환 시켜주자
            Resource resource = new ClassPathResource("zones_kr.csv");
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                    .stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return Zone.builder()
                                .city(split[0])
                                .localNameOfCity(split[1])
                                .province(split[2]).build();
                    }).collect(Collectors.toList());
            zoneRepository.saveAll(zoneList);
        }
    }

}
