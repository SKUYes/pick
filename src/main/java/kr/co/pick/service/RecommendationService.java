package kr.co.pick.service;

import kr.co.pick.dto.response.TagResDto;
import kr.co.pick.entity.Member;
import kr.co.pick.entity.Product;
import kr.co.pick.entity.Tag;
import kr.co.pick.entity.Wishlist;
import kr.co.pick.repository.MemberRepository;
import kr.co.pick.repository.ProductRepository;
import kr.co.pick.repository.TagRepository;
import kr.co.pick.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final WishlistRepository wishlistRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /* *******설문기반 추천******* */
    public List<Product> getRecommendedProductsByAgeAndGender(Long memberId){
        // 로그인 한 회원의 성별, 나이
        Member loginMember = memberRepository.findById(memberId).orElse(null);
        String memberGender = loginMember.getGender();
        int memberAge = loginMember.getAge();

        // 로그인 한 회원의 위시리스트
        List<Wishlist> memberWishlists = wishlistRepository.findByMember_Id(memberId);

        // 중복 제거 위해 HashSet 사용 => recommendProducts에 담긴 Product 객체들이 설문기반 추천하고자 하는 제품들 의미
        Set<Product> recommendProducts = new HashSet<>();

        // 로그인 한 회원의 나이 +-3살 회원들로 추천 로직 작성
        for(int age = memberAge-3; age <= memberAge+3; age++){
            // 해당 나이와 성별인 회원 전부 불러오기
            List<Member> similarMembers = memberRepository.findAllByGenderAndAge(memberGender, age);

            if(similarMembers == null){
                continue;
            }

            List<Wishlist> similarMemberWishlists = new ArrayList<>();
            // 위에서 찾은 회원의 위시리스트를 wishlist 변수에 추가
            for(Member member: similarMembers) {
                similarMemberWishlists.addAll(wishlistRepository.findByMember_Id(member.getId()));
            }

            for(Wishlist wishlist : similarMemberWishlists){
                if(!wishlist.getMember().getId().equals(memberId)){
                    Product product = wishlist.getProduct();
                    if(!isProductInLoginMemberWishlist(product, memberWishlists)){
                        recommendProducts.add(product);

                    }
                }
            }
        }

        return new ArrayList<>(recommendProducts);
    }

    /* *******유사한 고객이 추가한 위시리스트 Member ↔ Member******* */
    public List<Product> findProductsBySimilarMemberWishlist(Long loggedInMemberId) {
        // 현재 로그인한 회원 정보
        Optional<Member> loggedInMember = memberRepository.findById(loggedInMemberId);

        // 현재 로그인한 회원의 위시리스트
        List<Wishlist> loggedInMemberWishlist = wishlistRepository.findByMember_Id(loggedInMemberId);

        // 로그인한 회원의 위시리스트 안에 있는 제품 저장
        List<Product> loggedInMemberWishlistProducts = loggedInMemberWishlist.stream()
                .map(Wishlist::getProduct)
                .collect(Collectors.toList());

        System.out.println("** 로그인한 회원의 위시리스트 제품: ");
        loggedInMemberWishlistProducts.forEach(product -> System.out.println(product.getId()));


        // 로그인 한 회원의 태그 정보
        Tag loggedInMemberTag = loggedInMember.get().getTag();

        // 로그인 한 회원의 태그 정보를 List<Tag>형태에 저장
        List<Tag> loggedInTagList = new ArrayList<>();
        loggedInTagList.add(loggedInMemberTag);

        // 다른 회원 중에서 현재 로그인한 회원과 태그가 두 개 이상 겹치는 경우의 회원 ID와 가중치 가져오기
        Map<Long, Integer> memberWeights = findMemberWeights(loggedInTagList, loggedInMemberId);

        // 다른 회원의 위시리스트에서 현재 로그인한 회원의 위시리스트에 있는 제품과 겹치지 않는 제품 가져오기
        List<Product> recommendedProducts = findNonOverlappingProducts(memberWeights, loggedInMemberWishlistProducts, loggedInMemberId);
        System.out.println("=======> 최종 결과: ");
        recommendedProducts.forEach(product -> System.out.println(product.getId()));

        return recommendedProducts;
    }

    /* *******고객의 태그 정보와 비슷한 태그 정보 갖는 제품 추천 Member ↔ Product******* */
    public List<Product> findProductsByMemberTagInfo(Long loggedInMemberId) {
        // 현재 로그인한 회원 정보
        Optional<Member> loggedInMember = memberRepository.findById(loggedInMemberId);

        // 로그인 한 회원의 태그 정보
        Tag loggedInMemberTag = loggedInMember.get().getTag();

        // 태그 값 중에서 로그인한 회원의 태그 정보가 두 개 이상 겹치는 태그 ID와 가중치 가져오기
        List<Long> tagWeights = findTagWeights(loggedInMemberTag);
        System.out.println("=============> Total tagWeights: " + tagWeights);

        // 제품 DB에서 현재 로그인한 회원의 태그와 상당 부분 일치하는 제품 가져오기
        List<Product> recommendedProducts = findProductsByTagWeights(tagWeights);
        System.out.println("%%%% 최종 결과: ");
        recommendedProducts.forEach(product -> System.out.println(product.getId()));

        return recommendedProducts;
    }

    // 로그인한 회원의 위시리스트에 이미 있는 제품인지 확인
    private boolean isProductInLoginMemberWishlist(Product product, List<Wishlist> memberWishlists) {
        return memberWishlists.stream().anyMatch(wishlist -> wishlist.getProduct().getId().equals(product.getId()));
    }

    // 멤버 가중치 찾기
    private Map<Long, Integer> findMemberWeights(List<Tag> loggedInTagList, Long loggedInMemberId) {
        // 로그인 한 회원이 아닌 다른 회원 중에서 현재 로그인한 회원과 태그가 두 개 이상 겹치는 경우의 회원 ID와 가중치 가져오기
        List<Long> memberIds = wishlistRepository.findDistinctMemberIds(loggedInMemberId);  // wishlist에 추가한 게 있는 회원의 Id만 불러옴

        // 순서대로 다른 회원의 memberId값, 가중치 저장됨
        Map<Long, Integer> memberWeights = new HashMap<>();

        for (Long memberId : memberIds) {
            if (!memberId.equals(loggedInMemberId)) {
                Optional<Member> otherMember = memberRepository.findById(memberId);
                Tag otherMemberTag = otherMember.get().getTag();

                // otherMemberTagList 초기화
                List<Tag> otherMemberTagList = new ArrayList<>();
                otherMemberTagList.add(otherMemberTag);

                // stream 연산 및 tagCount 로직 수행
                long tagCount = loggedInTagList.stream()
                        .flatMapToLong(loggedInTag ->
                                otherMemberTagList.stream()
                                        .mapToLong(findOtherMemberTag ->
                                                (Objects.equals(loggedInTag.getSkinType(), findOtherMemberTag.getSkinType()) ? 1 : 0) +
                                                        (Objects.equals(loggedInTag.getSkinColor(), findOtherMemberTag.getSkinColor()) ? 1 : 0) +
                                                        (Objects.equals(loggedInTag.getPcolor(), findOtherMemberTag.getPcolor()) ? 1 : 0)
                                        )
                        )
                        .sum();


                memberWeights.put(memberId, (int) tagCount);
            }
        }
        return memberWeights;
    }

    // 가중치 기반 member ↔ member 추천 제품 불러오기
    private List<Product> findNonOverlappingProducts(Map<Long, Integer> memberWeights, List<Product> loggedInMemberWishlistProducts, Long loggedInMemberId) {

        // 로그인한 회원의 위시리스트 내 제품의 아이디 저장
        Set<Long> loggedInUserWishlistProductIds = loggedInMemberWishlistProducts.stream()
                .map(Product::getId)   // 로그인한 회원의 위시리스트에 있는 상품의 id 값을 매핑
                .collect(Collectors.toSet());   // 매핑한 값을 Set에 넣음

        for (Product product : loggedInMemberWishlistProducts) {
            loggedInUserWishlistProductIds.add(product.getId());
        }

        System.out.println("loggedInUserWishlistProductIds" + loggedInUserWishlistProductIds);

        // 추천 제품을 저장할 리스트
        List<Product> recommendedProducts = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : memberWeights.entrySet()) {
            Long memberId = entry.getKey();
            int weight = entry.getValue();

            // 현재 로그인한 회원과의 태그 겹침이 없고, 가중치가 2 이상인 경우
            if (weight >= 2) {
                System.out.println("가중치 2 이상의 memberID: " + memberId);
                // 다른 회원의 위시리스트에서 현재 로그인한 회원의 위시리스트에 있는 제품과 겹치지 않는 제품 가져오기
                List<Wishlist> otherMemberWishlist = wishlistRepository.findByMember_Id(memberId);

                List<Product> otherMemberWishlistProducts = new ArrayList<>();
                otherMemberWishlist.forEach(product -> otherMemberWishlistProducts.add(product.getProduct()));

                otherMemberWishlistProducts.forEach(product -> System.out.println("추천" + product.getId()));

                // 가져온 제품 목록 중 현재 로그인한 회원의 위시리스트에 있는 제품과 겹치지 않는 제품을 찾음
                List<Product> nonOverlappingProducts = otherMemberWishlistProducts.stream()
                        .filter(product -> !loggedInUserWishlistProductIds.contains(product.getId())) // 현재 로그인한 회원의 위시리스트에 이미 있는 제품과 겹치지 않는 제품들을 선택
                        .collect(Collectors.toList());  // stream()에서 필터링된 제품 리스트들을 리스트로 수집

                recommendedProducts.addAll(nonOverlappingProducts); // 찾아낸 겹치지 않는 제품들을 recommendedProducts 리스트에 추가
            }
        }

        return recommendedProducts;
    }

    // 태그 가중치 찾기
    private List<Long> findTagWeights(Tag tag) {

        // 태그 정보
        String skinType = tag.getSkinType();
        String skinColor = tag.getSkinColor();
        String pColor = tag.getPcolor();

        // 가중치가 2 이상인 tagId 값
        List<Long> tagWeights = new ArrayList<>();

        // 전체 태그 정보 List에 저장하기
        List<Tag> allTagList = tagRepository.findAll();

        // tagCount 로직 수행
        for (Tag now_tag : allTagList) {
            int tagCount = 0;

            // 각 필드별로 값이 같으면 해당 필드의 일치 개수를 가중치로 더함
            tagCount += (Objects.equals(skinType, now_tag.getSkinType()) ? 1 : 0)
                    + (Objects.equals(skinColor, now_tag.getSkinColor()) ? 1 : 0)
                    + (Objects.equals(pColor, now_tag.getPcolor()) ? 1 : 0);

            // 겹치는 필드 2개 이상인 제품에 대해서만 추천 하기 위해
            if(tagCount >= 2) {
                tagWeights.add(now_tag.getId());
            }
        }

        return tagWeights;
    }

    // 가중치 기반 member ↔ product 추천 제품 불러 오기
    public List<Product> findProductsByTagWeights(List<Long> tagWeights) {

        // Fetch Join을 사용하여 연관된 엔티티 로딩
        List<Product> allProductList = productRepository.findAllWithTags();

        // 중복을 방지하기 위해 Set 사용
        Set<Product> recommendedProducts = new HashSet<>();

        allProductList.forEach(product -> {
            if (tagWeights.contains(product.getTag().getId())) {
                recommendedProducts.add(product);
                System.out.println("----------> recommendedProducts에 추가된 제품: " + product.getId());
            }
        });

        // Set을 List로 변환하여 반환
        return new ArrayList<>(recommendedProducts);
    }

    //가중치 기반 product ↔ product 추천 제품 불러오기
    public List<Product> getRecommendedProductsForProduct(Long productId) {
        // 특정 제품 정보 가져오기
        Optional<Product> baseProduct = productRepository.findById(productId);

        if (baseProduct.isPresent()) {
            Product product = baseProduct.get();
            Tag productTag = product.getTag();

            // 태그 값 중에서 현재 제품의 태그 정보가 2개 이상 겹치는 태그 ID와 가중치 가져오기
            List<Long> tagWeights = findTagWeights(productTag);

            // 제품 DB에서 현재 제품의 태그와 상당 부분 일치하는 제품 가져오기
            List<Product> recommendedProducts = findProductsByTagWeights(tagWeights);

            // 현재 제품 제외
            recommendedProducts.remove(product);

            return recommendedProducts;
        } else {
            throw new NoSuchElementException("Product not found with ID: " + productId);
        }
    }

    // 로그인한 사용자와 같은 성별 + 비슷한 연령대가 많이 추가한 위시리스트 가중치 기반으로 제품 추천
    public List<Product> findProductsBySimilarAgeAndSameGender(Long loggedInMemberId) {
        // 현재 로그인한 회원 정보
        Optional<Member> loggedInMember = memberRepository.findById(loggedInMemberId);

        // 현재 로그인한 회원의 위시리스트
        List<Wishlist> loggedInMemberWishlist = wishlistRepository.findByMember_Id(loggedInMemberId);

        // 로그인한 회원의 위시리스트 안에 있는 제품 저장
        List<Product> loggedInMemberWishlistProducts = loggedInMemberWishlist.stream()
                .map(Wishlist::getProduct)
                .collect(Collectors.toList());

        System.out.println("** 로그인한 회원의 위시리스트 제품: ");
        loggedInMemberWishlistProducts.forEach(product -> System.out.println(product.getId()));

        // 로그인 한 회원의 태그 정보
        Tag loggedInMemberTag = loggedInMember.get().getTag();

        // 로그인 한 회원의 태그 정보를 List<Tag>형태에 저장
        List<Tag> loggedInTagList = new ArrayList<>();
        loggedInTagList.add(loggedInMemberTag);

        // 다른 회원 중에서 현재 로그인한 회원과 성별이 같고 비슷한 연령대의 가중치 가져오기
        Map<Long, Integer> memberWeights = findSimilarMemberWeights(loggedInTagList, loggedInMemberId);

        // 다른 회원의 위시리스트에서 현재 로그인한 회원의 위시리스트에 있는 제품과 겹치지 않는 제품 가져오기
        List<Product> recommendedProducts = findNonOverlappingProducts(memberWeights, loggedInMemberWishlistProducts, loggedInMemberId);
        System.out.println("=======> 최종 결과: ");
        recommendedProducts.forEach(product -> System.out.println(product.getId()));

        return recommendedProducts;
    }

    // 동일한 성별 + 비슷한 연령대 회원의 가중치 찾기
    private Map<Long, Integer> findSimilarMemberWeights(List<Tag> loggedInTagList, Long loggedInMemberId) {

//        // 로그인 한 회원이 아닌 다른 회원 중에서 현재 로그인한 회원과 태그가 두 개 이상 겹치는 경우의 회원 ID와 가중치 가져오기
//        List<Long> memberIds = wishlistRepository.findDistinctMemberIds(loggedInMemberId);  // wishlist에 추가한 게 있는 회원의 Id만 불러옴

        Optional<Member> loginMember = memberRepository.findById(loggedInMemberId);

        // 로그인 한 회원과 동일한 성별, -5 ~ +5 사이의 나이를 가진 회원 ID 가져오기
        List<Long> memberIds = memberRepository.findSimilarMembers(loginMember.get().getGender(),
                loginMember.get().getAge() - 5,
                loginMember.get().getAge() + 5,
                loggedInMemberId);

        // 순서대로 다른 회원의 memberId값, 가중치 저장됨
        Map<Long, Integer> memberWeights = new HashMap<>();

        for (Long memberId : memberIds) {
            if (!memberId.equals(loggedInMemberId)) {
                Optional<Member> otherMember = memberRepository.findById(memberId);
                Tag otherMemberTag = otherMember.get().getTag();

                // otherMemberTagList 초기화
                List<Tag> otherMemberTagList = new ArrayList<>();
                otherMemberTagList.add(otherMemberTag);

                // stream 연산 및 tagCount 로직 수행
                long tagCount = loggedInTagList.stream()
                        .flatMapToLong(loggedInTag ->
                                otherMemberTagList.stream()
                                        .mapToLong(findOtherMemberTag ->
                                                (Objects.equals(loggedInTag.getSkinType(), findOtherMemberTag.getSkinType()) ? 1 : 0) +
                                                        (Objects.equals(loggedInTag.getSkinColor(), findOtherMemberTag.getSkinColor()) ? 1 : 0) +
                                                        (Objects.equals(loggedInTag.getPcolor(), findOtherMemberTag.getPcolor()) ? 1 : 0)
                                        )
                        )
                        .sum();


                memberWeights.put(memberId, (int) tagCount);
            }
        }
        return memberWeights;
    }
}