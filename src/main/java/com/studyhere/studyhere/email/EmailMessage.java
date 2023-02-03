package com.studyhere.studyhere.email;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {
    //누구에게 보내는지
    private String to;
    //제목
    private String subject;
    //내용
    private String message;

}