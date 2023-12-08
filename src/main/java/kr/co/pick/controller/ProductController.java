package kr.co.pick.controller;


import kr.co.pick.entity.Product;
import kr.co.pick.service.ProductService;
import lombok.RequiredArgsConstructor;
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


}
