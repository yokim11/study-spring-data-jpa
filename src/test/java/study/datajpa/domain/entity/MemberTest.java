package study.datajpa.domain.entity;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member(20, "member1", teamA);
        Member member2 = new Member(30, "member2", teamA);
        Member member3 = new Member(40, "member3", teamB);
        Member member4 = new Member(50, "member4", teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
            .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    void JpaEventBaseEntity() throws InterruptedException {
        //given
        Member member = new Member(10, "member1");
        memberRepository.save(member);

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); //@PreUpdate
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.createDate = " + findMember.getCreateDate());
        System.out.println("findMember.createdBy = " + findMember.getCreatedBy());
        System.out.println("findMember.updateDate = " + findMember.getUpdateDate());
        System.out.println("findMember.updatedBy = " + findMember.getUpdatedBy());
    }
}