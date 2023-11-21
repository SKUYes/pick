package kr.co.pick.repository;

import kr.co.pick.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

//    List<Product> findAllBySubCategory(Long subCategoryId);
//
//    List<Product> findAllByTexture(Long textureId);
//
//    List<Product> findAllByTag(Long tagId);
}
