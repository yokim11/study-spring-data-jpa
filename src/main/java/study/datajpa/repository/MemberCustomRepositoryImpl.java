package study.datajpa.repository;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.domain.entity.Member;

@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {

        return em.createQuery("select m from Member m")
            .getResultList();
    }
}
