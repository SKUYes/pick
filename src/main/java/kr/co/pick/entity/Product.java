package kr.co.pick.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private String price;

    @OneToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @OneToOne
    @JoinColumn(name = "texture_id")
    private Texture texture;

    @OneToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public Product(String name,
                   String price,
                   SubCategory subCategory,
                   Texture texture,
                   Tag tag) {
        this.name = name;
        this.price = price;
        this.subCategory = subCategory;
        this.texture = texture;
        this.tag = tag;
    }
}
