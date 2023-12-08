package kr.co.pick.repository;

import kr.co.pick.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findBySkinColorAndSkinTypeAndPcolor(String SkinColor, String SkinType, String pcolor);
}
