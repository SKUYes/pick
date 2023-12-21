package kr.co.pick.repository;

import jakarta.transaction.Transactional;
import kr.co.pick.dto.response.ProductResDto;
import kr.co.pick.entity.Member;
import kr.co.pick.entity.Product;
import kr.co.pick.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    // member_id가 추가한 위시리스트 내역 불러오기
    List<Wishlist> findByMember_Id(Long memberId);

    // 위시리스트 삭제
    @Modifying
    @Query("DELETE FROM Wishlist w " +
            "WHERE w.member.id = :loginMemberId " +
            "AND w.product.id = :productId")
    @Transactional
    public void deleteWishlistById(Long productId, Long loginMemberId);

    Optional<Wishlist> findByMemberAndProduct(Member member, Product product);

    // 위시리스트 내에서 loggedInMemberId 제외 하고 멤버 아이디 찾기
    @Query("SELECT DISTINCT w.member.id FROM Wishlist w WHERE w.member.id <> :loggedInMemberId")
    List<Long> findDistinctMemberIds(@Param("loggedInMemberId") Long loggedInMemberId);

    // member_id를 불러와서 위시리스트에 추가한 제품 찾기
    @Query("SELECT (w.product.id, w.product.name) FROM Wishlist w WHERE w.member.id = :memberId")
    List<ProductResDto> findProductsByMemberId(Long memberId);


}
