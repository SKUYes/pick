package kr.co.pick.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResDto {

    private Long productId;

    private String name;

    private String price;

    private Long subCategoryId;

    private String subCategoryName;

    private Long categoryId;

    private String categoryName;

    private Long textureId;

    private String textureName;

    private Long tagId;

    private String productSkinType;

    private String productSkinColor;

    private String productPcolor;

}

