package com.studyhere.studyhere.domain.dto;

import com.studyhere.studyhere.domain.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class Profile {

    /**프로필 정보**/
    @Length(max=35)
    private String bio;
    @Length(max=50)
    private String url;
    /**직업**/
    @Length(max=50)
    private String occupation;
    /**거주지**/
    @Length(max=50)
    private String location;

    private String profileImage;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
        this.profileImage = account.getProfileImage();
    }
}
