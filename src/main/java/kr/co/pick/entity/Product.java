package kr.co.pick.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "texture_id")
    private Texture texture;

    @ManyToOne
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
