package kr.co.pick.controller;

import jakarta.servlet.http.HttpSession;
import kr.co.pick.dto.response.MemberResDto;
import kr.co.pick.dto.response.ProductResDto;
import kr.co.pick.entity.Product;
import kr.co.pick.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    // 추천 테스트
    @GetMapping("/recommendBySimilarMemberWishlist")
     public void getRecommededBySimilarMemberWishlist(HttpSession session) {
        MemberResDto loginMember = (MemberResDto)session.getAttribute("loginMember");

        recommendationService.findProductsByMemberTagInfo(loginMember.getMemberId());

        System.out.println("*****Service 실행 끝");
    }
}
