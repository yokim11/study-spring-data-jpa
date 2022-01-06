package study.datajpa.repository;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.controller.dto.MemberDto;
import study.datajpa.domain.entity.Member;
import study.datajpa.domain.entity.Team;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        member1.setUsername("member!!!!!");

    }

    @Test
    void countByUsername() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        long count = memberRepository.countByUsername("member1");
        System.out.println("count = " + count);
    }

    @Test
    void existsByMemberId() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        boolean b = memberRepository.existsById(member1.getId());
        System.out.println("memberId = " + member1.getId() + ", b = " + b);
    }

    @Test
    void findMemberDto() {
        Team team1 = new Team("AAA");
        teamRepository.save(team1);

        Team team2 = new Team("BBB");
        teamRepository.save(team2);

        Member member1 = new Member("member1");
        member1.setTeam(team1);
        Member member2 = new Member("member2");
        member2.setTeam(team2);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();
        for (MemberDto memberDto : memberDtos) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    void findByNames() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> names = memberRepository.findByNames(Arrays.asList("member1", "member2"));
        for (Member name : names) {
            System.out.println("name = " + name);
        }
    }

    @Test
    void returnType() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> names = memberRepository.findListByUsername("member1");
        for (Member name : names) {
            System.out.println("name = " + name);
        }
    }

    @Test
    void pageing() {
        //given
        memberRepository.save(new Member(10, "member1"));
        memberRepository.save(new Member(10, "member2"));
        memberRepository.save(new Member(10, "member3"));
        memberRepository.save(new Member(10, "member4"));
        memberRepository.save(new Member(10, "member5"));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> dtos = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> contents = page.getContent();
        long totalElements = page.getTotalElements();
        System.out.println("totalElements = " + totalElements);
        for (Member content : page) {
            System.out.println("content = " + content);
        }

        assertThat(page.getSize()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void bulkAgePlus() {
        //given
        memberRepository.save(new Member(10, "member1"));
        memberRepository.save(new Member(19, "member2"));
        memberRepository.save(new Member(20, "member3"));
        memberRepository.save(new Member(21, "member4"));
        memberRepository.save(new Member(40, "member5"));

        int resultCount = memberRepository.bulkAgePlus(20);
        assertThat(resultCount).isEqualTo(3);

        Member member5 = memberRepository.findMemberByUsername("member5");
        System.out.println("member5 = " + member5);

        em.flush();
        em.clear();

        Member member51 = memberRepository.findMemberByUsername("member5");
        System.out.println("member5 = " + member51);

    }

    @Test
    void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team team1 = new Team("teamA");
        Team team2 = new Team("teamB");
        teamRepository.save(team1);
        teamRepository.save(team2);

        Member member1 = new Member(10,"member1", team1);
        Member member2 = new Member(10,"member2", team2);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
//        List<Member> all = memberRepository.findAll();
//        List<Member> all = memberRepository.findMemberFetchJoin();
        List<Member> all = memberRepository.findMemberEntityGraph();

        for (Member member : all) {
            System.out.println("member = " + member);
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member(10, "member1"));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");
        em.flush();
    }


    @Test
    void lock() {
        //given
        Member member1 = memberRepository.save(new Member(10, "member1"));
        em.flush();
        em.clear();

        //when
        List<Member> findMember = memberRepository.findLockByUsername(member1.getUsername());
        em.flush();
    }

    @Test
    void callCustom() {
        List<Member> findMember = memberRepository.findMemberCustom();
    }

    @Test
    void findByNativeQuery() {
        //given
        Member member1 = memberRepository.save(new Member(10, "member1"));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findByNativeQuery(member1.getUsername());
        System.out.println("findMember = " + findMember);
    }

}