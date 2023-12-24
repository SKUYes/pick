package kr.co.pick.service;

import kr.co.pick.dto.response.ProductResDto;
import kr.co.pick.entity.*;
import kr.co.pick.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    // 검색
    public List<ProductResDto> searchProducts(String searchType, String searchValue) {
        if (searchValue == null || searchValue.isEmpty()) {
            // 검색어가 비어있는 경우, 전체 상품 조회
            return productRepository.findAll()
                    .stream()
                    .map(this::toDto)
                    .toList();
        }
        if ("productName".equals(searchType)) {
            return productRepository.findByNameContainingIgnoreCase(searchValue)
                    .stream()
                    .map(this::toDto)
                    .toList();
        } else if ("skinType".equals(searchType)) {
            switch (searchValue) {
                case "건성":
                    searchValue = "dry_skin";
                    break;
                case "지성":
                    searchValue = "oily_skin";
                    break;
                case "수부지":
                    searchValue = "moisture_deficit_skin";
                    break;
                case "민감성":
                    searchValue = "sensitive_skin";
                    break;
                // 다른 케이스에 대한 처리가 필요한 경우 추가할 수 있습니다.
                default:
                    // 기본 처리 (예: 아무런 변환 없이 그대로 사용)
                    break;
            }
            return productRepository.findByTag_SkinType(searchValue)
                    .stream()
                    .map(this::toDto)
                    .toList();
        } else if ("skinColor".equals(searchType)) {
            if(searchValue.contains("13")){
                searchValue = "13";
            } else if (searchValue.contains("17")) {
                searchValue = "17";
            } else if (searchValue.contains("21")) {
                searchValue = "21";
            } else if (searchValue.contains("23")) {
                searchValue = "23";
            } else if (searchValue.contains("25")) {
                searchValue = "25";
            }
            return productRepository.findByTag_SkinColor(searchValue)
                    .stream()
                    .map(this::toDto)
                    .toList();
        } else if ("pColor".equals(searchType)) {
            if(searchValue.contains("봄")) {
                searchValue = "spring_type";
            } else if (searchValue.contains("여름")) {
                searchValue = "summer_type";
            } else if (searchValue.contains("가을")) {
                searchValue = "autumn_type";
            } else if (searchValue.contains("겨울")) {
                searchValue = "winter_type";
            }
            return productRepository.findByTag_pcolor(searchValue)
                    .stream()
                    .map(this::toDto)
                    .toList();
        } else {
            // 검색 조건이 잘못된 경우 빈 페이지 객체 반환
            List<ProductResDto> recommendList = new ArrayList<>();
            return recommendList;
        }
    }

    private ProductResDto toDto(Product product) {
        ProductResDto.ProductResDtoBuilder builder = ProductResDto.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice());

        SubCategory subCategory = product.getSubCategory();
        if (subCategory != null) {
            builder.subCategoryId(subCategory.getId())
                    .subCategoryName(subCategory.getName());

            Category category = subCategory.getCategory();
            if (category != null) {
                builder.categoryId(category.getId())
                        .categoryName(category.getName());
            }
        }

        Texture texture = product.getTexture();
        if (texture != null) {
            builder.textureId(texture.getId())
                    .textureName(texture.getName());
        }

        Tag tag = product.getTag();
        if (tag != null) {
            builder.tagId(tag.getId())
                    .productSkinType(tag.getSkinType())
                    .productSkinColor(tag.getSkinColor())
                    .productPcolor(tag.getPcolor());
        }

        return builder.build();
    }


}
