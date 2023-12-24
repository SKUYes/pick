package kr.co.pick.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReqDto {

    private String name;

    private String price;

    private Long subCategoryId;

    private Long textureId;

    private Long tagId;
}
