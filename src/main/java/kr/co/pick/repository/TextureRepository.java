package kr.co.pick.repository;

import kr.co.pick.entity.Texture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextureRepository extends JpaRepository<Texture, Long> {
}
