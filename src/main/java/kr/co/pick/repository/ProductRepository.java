package kr.co.pick.repository;

import kr.co.pick.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllBySubCategory_Id(Long subCategoryId);

//    List<Product> findAllByTexture(Long textureId);
//
//    List<Product> findAllByTag(Long tagId);

    // 검색 옵션 없이 모든 제품을 페이징 처리하여 반환
    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllBySubCategory_Id(Long subCategoryId, Pageable pageable);

    Page<Product> findAllByTexture_Id(Long textureId, Pageable pageable);

    Page<Product> findAllByTag_Id(Long tagId, Pageable pageable);

}
