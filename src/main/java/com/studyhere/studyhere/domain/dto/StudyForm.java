package com.studyhere.studyhere.domain.dto;

import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.entity.Study;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
public class StudyForm {

    public static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = VALID_PATH_PATTERN)
    private String path;

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;



    public static Study of(StudyForm studyForm) {
        return Study.builder()
                .path(studyForm.getPath())
                .title(studyForm.getTitle())
                .shortDescription(studyForm.getShortDescription())
                .fullDescription(studyForm.getFullDescription()).build();
    }

}
