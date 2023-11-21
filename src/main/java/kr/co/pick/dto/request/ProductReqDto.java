package kr.co.pick.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.co.pick.entity.SubCategory;
import kr.co.pick.entity.Tag;
import kr.co.pick.entity.Texture;
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
