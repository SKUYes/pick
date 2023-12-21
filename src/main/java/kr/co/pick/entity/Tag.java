package kr.co.pick.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "skin_type")
    private String skinType;

    @Column(name = "skin_color")
    private String skinColor;

    @Column(name = "pcolor")
    private String pcolor;

    @Builder
    public Tag(String skinType,
               String skinColor,
               String pcolor) {
        this.skinType = skinType;
        this.skinColor = skinColor;
        this.pcolor = pcolor;
    }
}
