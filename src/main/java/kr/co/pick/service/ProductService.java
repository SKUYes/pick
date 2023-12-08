package kr.co.pick.service;

import kr.co.pick.dto.response.ProductResDto;
import kr.co.pick.entity.Product;
import kr.co.pick.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
