package kr.co.pick.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Texture extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "texture_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Builder
    public Texture(String name) {
        this.name = name;
    }
}
