package kr.co.pick.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.pick.dto.response.MemberResDto;
import kr.co.pick.entity.Member;
import kr.co.pick.entity.Product;
import kr.co.pick.service.MemberService;
import kr.co.pick.service.ProductService;
import kr.co.pick.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final ProductService productService;
    private final MemberService memberService;

    // 위시리스트 내용 출력
    @GetMapping("/view")
    public String viewWishlist(Model model , HttpSession session){
        // 현재 로그인한 회원의 아이디
        MemberResDto loginMember  = (MemberResDto)session.getAttribute("loginMember");
        // 로그인 한 상태인지 확인
        if(loginMember == null){
            return "member/login";
        }
//        System.out.println( loginMember.getMemberId());
        Long loginMemberId = loginMember.getMemberId();

        // 로그인 한 회원의 위시리스트
        List<Product> productInWishlist = wishlistService.getWishlistProductByMemberIdentity(loginMemberId);
//        System.out.println("productInWishlist:" + productInWishlist);
        model.addAttribute("productInWishlist", productInWishlist);

        return "member/wishlist";
    }

    // 위시리스트에서 삭제
    @GetMapping("/delete/{productId}")
    public String deleteWishlistItem(@PathVariable Long productId, HttpSession session){
//        System.out.println(productId);

        // 현재 로그인한 회원의 아이디
        MemberResDto loginMember = (MemberResDto)session.getAttribute("loginMember") ;
        Long loginMemberId = loginMember.getMemberId();

        wishlistService.deleteItem(productId, loginMemberId);
        return "redirect:/wishlist/view";
    }

    // 위시리스트에 추가
    @GetMapping("/add/{productId}")
    public String addToWishlist(@PathVariable Long productId, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes){
        MemberResDto loginMember = (MemberResDto)session.getAttribute("loginMember");

        // 현재 로그인이 안되어 있다면
        if(loginMember == null){
            return "member/login";
        }

        // 현재 페이지로 다시 이동
        String referer = request.getHeader("Referer");


        // 로그인이 되어 있다면
        try{
            Product product = productService.findProductById(productId);
            Member member = memberService.readMember(loginMember.getIdentity());

            // 로그인 한 회원의 위시리스트에 추가
            wishlistService.addProductToWishlist(member, product);

            return "redirect:" + referer;

        } catch (Exception e){
            // 예외 처리
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + referer;
        }

    }
}
