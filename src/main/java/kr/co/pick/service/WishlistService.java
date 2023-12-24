package kr.co.pick.service;

import kr.co.pick.entity.Member;
import kr.co.pick.entity.Product;
import kr.co.pick.entity.Wishlist;
import kr.co.pick.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MemberService memberService;

    // 위시리스트에 추가한 제품의 정보 읽어오기
    public List<Product> getWishlistProductByMemberIdentity(Long loginMemberId) {

        List<Wishlist> wishlistItems = wishlistRepository.findByMember_Id(loginMemberId);
        return wishlistItems.stream().map(Wishlist::getProduct).collect(Collectors.toList());
    }

    // 위시리스트에서 삭제
    public void deleteItem(Long productId, Long loginMemberId){
        System.out.println("productId: " + productId + "loginMemberId: " + loginMemberId );
        wishlistRepository.deleteWishlistById(productId, loginMemberId);
    }

    // 위시리스트에 추가
    public void addProductToWishlist(Member member, Product product){
//        System.out.println("loginMemberResDto: " + memberResDto + "productResDto: " + productResDto);
        // 위시리스트에 중복 추가인지 확인
        isWishlist(member, product);

        Wishlist wishlist = new Wishlist();
        wishlist.setMember(member);
        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);

    }

    // 위시리스트에 중복 체크
    private void isWishlist(Member member, Product product){

        if(wishlistRepository.findByMemberAndProduct(member, product).isPresent()) {
            throw new RuntimeException("이미 위시리스트에 존재합니다.");
        }
    }
}
