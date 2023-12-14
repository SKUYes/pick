package kr.co.pick.repository;

import jakarta.transaction.Transactional;
import kr.co.pick.dto.response.MemberResDto;
import kr.co.pick.entity.Member;
import kr.co.pick.entity.Product;
import kr.co.pick.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByMember_Id(Long memberId);

    // 위시리스트 삭제
    @Modifying
    @Query("DELETE FROM Wishlist w " +
            "WHERE w.member.id = :loginMemberId " +
            "AND w.product.id = :productId")
    @Transactional
    public void deleteWishlistById(Long productId, Long loginMemberId);

    Optional<Wishlist> findByMemberAndProduct(Member member, Product product);

//    List<Wishlist>  findByMemberGenderAndAgeRange(String gender, int ageRange);

}
