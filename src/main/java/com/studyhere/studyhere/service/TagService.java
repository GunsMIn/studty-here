package com.studyhere.studyhere.service;

import com.studyhere.studyhere.domain.entity.Tag;
import com.studyhere.studyhere.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    /**요청으로 들어온 관심주제를 rep에서 조회 후
     * 없다면 save()후 tag 반환
     * 이미 존재하면 조회 반환**/
    public Tag findOrCreateNew(String tagTitle) {
        Tag tag = tagRepository.findByTitle(tagTitle);
        if (tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagTitle).build());
        }
        return tag;
    }




}