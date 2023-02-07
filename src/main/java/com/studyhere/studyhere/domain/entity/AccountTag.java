//
//package com.studyhere.studyhere.domain.entity;
//
//import com.studyhere.studyhere.domain.dto.TagForm;
//import lombok.*;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@Setter
//@EqualsAndHashCode(of = "id")
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class AccountTag {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "account_id")
//    private Account account;
//
//    @ManyToOne(cascade = CascadeType.REMOVE)
//    @JoinColumn(name = "tag_id")
//    private Tag tag;
//
//
//    public AccountTag(Account account, Tag tag) {
//        this.account = account;
//        this.tag = tag;
//    }
//
//    /**연관관계 편의 메서드**/
//    public static AccountTag createAccountTag(Account account,Tag tag) {
//        //accountTag에 회원과 관심주제 insert
//        AccountTag accountTag = new AccountTag(account,tag);
//        return accountTag;
//    }
//}
//
