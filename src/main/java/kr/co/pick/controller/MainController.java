package kr.co.pick.controller;

import jakarta.servlet.http.HttpSession;
import kr.co.pick.dto.response.MemberResDto;
import kr.co.pick.entity.Product;
import kr.co.pick.service.ProductService;
import kr.co.pick.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RecommendationService recommendationService;
    private final ProductService productService;

    @GetMapping("/home")
    public void home(HttpSession session, Model model) {

        // productDB에서 랜덤하게 12개 불러오기
        List<Product> randomProducts = productService.randomProducts();
        model.addAttribute("randomProducts", randomProducts);

        // 로그인이 된 상태일 때 추천 서비스 실행
        if(session.getAttribute("loginMember") != null){
            MemberResDto loginMember = (MemberResDto)session.getAttribute("loginMember");
//        System.out.println("현재 로그인한 회원: " + loginMember.getMemberId());
            
            // 설문 기반 추천
            List<Product> recommendProducts = recommendationService.getRecommendedProductsByAgeAndGender(loginMember.getMemberId());
            model.addAttribute("wishlistItemsByGenderAndAge", recommendProducts);
        }

    }

    @GetMapping("/")
    public String goHome() {
        return "redirect:/home";
    }
}
