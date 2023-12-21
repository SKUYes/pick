package kr.co.pick.service;

import kr.co.pick.dto.response.ProductResDto;
import kr.co.pick.entity.Product;
import kr.co.pick.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 아이디 값으로 제품 조회
    public Product findProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        Product product = optionalProduct.orElse(null);

        return product;
    }

    // 모든 상품 조회
    public List<ProductResDto> readAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 서브 카테고리로 모든 상품 조회
    public List<Product> readAllProductsBySub(Long subCategoryId) {
        return productRepository.findAllBySubCategory_Id(subCategoryId);
    }

    // Product 랜덤하게 12개 불러오기
    public List<Product> randomProducts(){
        // ProductDB에서 모든 제품을 가져옴
        List<Product> allProducts = productRepository.findAll();

        // 랜덤하게 섞음
        Collections.shuffle(allProducts);

        // 앞에서 12개만 선택하여 반환
        return allProducts.subList(0, 12);
    }

    public Page<ProductResDto> searchProducts(String searchType, String searchValue, Pageable pageable) {
        if (searchValue == null || searchValue.isEmpty()) {
            // 검색어가 비어있는 경우, 전체 상품 조회
            return productRepository.findAll(pageable)
                    .map(this::toDto);
        }

        if ("subCategoryId".equals(searchType)) {
            Long subCategoryId = Long.parseLong(searchValue);
            return productRepository.findAllBySubCategory_Id(subCategoryId, pageable)
                    .map(this::toDto);
        } else if ("textureId".equals(searchType)) {
            Long textureId = Long.parseLong(searchValue);
            return productRepository.findAllByTexture_Id(textureId, pageable)
                    .map(this::toDto);
        } else if ("tagId".equals(searchType)) {
            Long tagId = Long.parseLong(searchValue);
            return productRepository.findAllByTag_Id(tagId, pageable)
                    .map(this::toDto);
        } else {
            // 검색 조건이 잘못된 경우 빈 페이지 객체 반환
            return Page.empty(pageable);
        }
    }

//    // Texture 로 모든 상품 조회
//    public List<ProductResDto> readAllProductsByTex(Long textureId) {
//        return productRepository.findAllByTexture(textureId)
//                .stream()
//                .map(this::toDto)
//                .toList();
//    }
//
//    // Tag 로 모든 상품 조회
//    public List<ProductResDto> readAllProductsByTag(Long tagId) {
//        return productRepository.findAllByTag(tagId)
//                .stream()
//                .map(this::toDto)
//                .toList();
//    }


    private ProductResDto toDto(Product product) {
        return ProductResDto.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .subCategoryId(product.getSubCategory().getId())
                .subCategoryName(product.getSubCategory().getName())
                .categoryId(product.getSubCategory().getCategory().getId())
                .categoryName(product.getSubCategory().getCategory().getName())
                .textureId(product.getTexture().getId())
                .textureName(product.getTexture().getName())
                .tagId(product.getTag().getId())
                .productSkinType(product.getTag().getSkinType())
                .productSkinColor(product.getTag().getSkinColor())
                .productPcolor(product.getTag().getPcolor())
                .build();
    }

}
