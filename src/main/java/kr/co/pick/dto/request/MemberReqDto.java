package kr.co.pick.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReqDto {

    private String identity;

    private String password;

    private String name;

    private String nickname;

    private String gender;

    private LocalDate birthDate;

    private Long tagId;

}
