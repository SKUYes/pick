package kr.co.pick.repository;

import kr.co.pick.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllBySubCategory_Id(Long subCategoryId);

    // 검색 옵션 없이 모든 제품을 페이징 처리하여 반환
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p INNER JOIN FETCH p.tag")
    List<Product> findAllWithTags();


    // 검색 로직에 사용
    List<Product> findByTag_SkinType(String skinType);
    List<Product> findByTag_SkinColor(String skinColor);
    List<Product> findByTag_pcolor(String pcolor);
    List<Product> findByNameContainingIgnoreCase(String name);

}
