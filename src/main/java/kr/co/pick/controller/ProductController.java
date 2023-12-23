package kr.co.pick.controller;


import jakarta.servlet.http.HttpSession;
import kr.co.pick.dto.response.MemberResDto;
import kr.co.pick.dto.response.ProductResDto;
import kr.co.pick.entity.Product;
import kr.co.pick.service.ProductService;
import kr.co.pick.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final RecommendationService recommendationService;

    // 제품 목록
    @GetMapping("/productList/{subCategoryId}")
    public String viewProductList(@PathVariable(required = false) Long subCategoryId, Model model){
        System.out.println("ProductController에서 subCategoryId: " + subCategoryId);
        List<Product> productList = productService.readAllProductsBySub(subCategoryId);
        System.out.println(productList);
        model.addAttribute("products", productList);
        model.addAttribute("subCategoryId", subCategoryId);

        return "product/productList";
    }

    @GetMapping("/list")
    public String readAllProduct(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchValue,
            @RequestParam(defaultValue = "0") int page, // 현재 페이지. 기본값 0
            @RequestParam(defaultValue = "10") int size, // 한 페이지에 보여질 아이템 수. 기본값 10
            Model model) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductResDto> productListBySearch = productService.searchProducts(searchType, searchValue, pageable);
        System.out.println("----------productList: " + productListBySearch);

        // productListBySearch의 getContent()를 사용하여 List<ProductResDto>를 가져오는 대신, getContent()를 사용하지 않고 바로 사용할 수 있도록 수정
        model.addAttribute("productListBySearch", productListBySearch);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productListBySearch.getTotalPages());

        return "product/search-list";
    }

    // 제품 상세 페이지
    @GetMapping("/productDetail/{productId}")
    public String viewProductDetail(@PathVariable(required=false) Long productId, HttpSession session, Model model){
        Product product = productService.findProductById(productId);
        model.addAttribute("productDetail", product);


        if(session.getAttribute("loginMember") != null){
            MemberResDto loginMember = (MemberResDto) session.getAttribute("loginMember");

            // member ↔ member 추천
            List<Product> recommendByOtherMemberWishlist = recommendationService.findProductsBySimilarMemberWishlist(loginMember.getMemberId());
            model.addAttribute("recommendByOtherMemberWishlist", recommendByOtherMemberWishlist);

            // member ↔ product 추천
            List<Product> recommendByMemberTagInfo = recommendationService.findProductsByMemberTagInfo(loginMember.getMemberId());
            model.addAttribute("recommendByMemberTagInfo", recommendByMemberTagInfo);

            // product ↔ product 추천
            List<Product> recommendByOtherProduct = recommendationService.getRecommendedProductsForProduct(product.getId());
            model.addAttribute("recommendByOtherProduct", recommendByOtherProduct);
        }

        System.out.println("*****Service 실행 끝");

        return "product/product-details";
    }


}
