package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import study.datajpa.controller.dto.MemberDto;
import study.datajpa.domain.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    //생략도 가능하다
    //@Query("select count(m) from Member m where m.username = :username")
    long countByUsername(@Param("username") String username);


    boolean existsById(@Param("id") Long memberId);

    @Query("select new study.datajpa.controller.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

//    Slice<Member> findByAge(int age, Pageable pageable);
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();


    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    //select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);
}
