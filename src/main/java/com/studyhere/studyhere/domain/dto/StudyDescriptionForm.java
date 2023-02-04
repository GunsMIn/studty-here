package com.studyhere.studyhere.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class StudyDescriptionForm {
    @NotBlank
    private String title;
    @NotBlank
    @Length(max = 100)
    private String shortDescription;
    @NotBlank
    private String fullDescription;
}
