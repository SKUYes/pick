package kr.co.pick.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResDto {

    private Long memberId;

    private String identity;

    private String password;

    private String name;

    private String nickname;

    private String gender;

    private LocalDate birthDate;

    private Long tagId;

    private String memberSkinType;

    private String memberSkinColor;

    private String memberPcolor;


}
