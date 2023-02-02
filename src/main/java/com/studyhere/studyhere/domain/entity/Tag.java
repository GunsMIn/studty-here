package com.studyhere.studyhere.domain.entity;

import com.studyhere.studyhere.domain.dto.TagForm;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    @OneToMany(mappedBy = "tag")
    private List<AccountTag> accountTags = new ArrayList<>();


    public static Tag of(TagForm tagForm) {
        return Tag.builder()
                .title(tagForm.getTagTitle())
                .build();
    }




}