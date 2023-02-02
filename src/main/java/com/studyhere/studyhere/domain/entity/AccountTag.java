
package com.studyhere.studyhere.domain.entity;

import com.studyhere.studyhere.domain.dto.TagForm;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    /**연관관계 편의 메서드**/
    public static AccountTag createAccountTag(Account account,Tag tag) {
        AccountTag accountTag = new AccountTag();
        accountTag.setAccount(account);
        accountTag.setTag(tag);
        return accountTag;
    }
}

