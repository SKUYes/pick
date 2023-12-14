package kr.co.pick.controller;


import kr.co.pick.dto.response.ProductResDto;
import kr.co.pick.entity.Product;
import kr.co.pick.service.ProductService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

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

        Page<ProductResDto> productList = productService.searchProducts(searchType, searchValue, pageable);

        // productList의 getContent()를 사용하여 List<ProductResDto>를 가져오는 대신, getContent()를 사용하지 않고 바로 사용할 수 있도록 수정
        model.addAttribute("productList", productList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productList.getTotalPages());

        return "product/list";
    }

    @GetMapping("/productDetail/{productId}")
    public String viewProductDetail(@PathVariable(required=false) Long productId, Model model){
        System.out.println("viewProductDetail: " + productId);
        Product product = productService.findProductById(productId);
        System.out.println(product);
        model.addAttribute("productDetail", product);

        return "product/product-details";
    }

}
