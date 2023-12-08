package kr.co.pick.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagReqDto {

    private String skinType;

    private String skinColor;

    private String pcolor;

}
