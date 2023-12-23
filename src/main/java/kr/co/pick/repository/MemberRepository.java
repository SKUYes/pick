package kr.co.pick.repository;

import kr.co.pick.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdentity(String identity);

    List<Member> findAllByGenderAndAge(String gender, int age);

    @Query("SELECT m.id FROM Member m " +
            "WHERE m.gender = :gender " +
            "AND m.age >= :minAge AND m.age <= :maxAge " +
            "AND m.id <> :memberId")
    List<Long> findSimilarMembers(@Param("gender") String gender,
                                    @Param("minAge") int minAge,
                                    @Param("maxAge") int maxAge,
                                    @Param("memberId") Long memberId);

}
