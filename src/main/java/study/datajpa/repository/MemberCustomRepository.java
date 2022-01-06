package study.datajpa.repository;

import java.util.List;
import study.datajpa.domain.entity.Member;

public interface MemberCustomRepository {

    List<Member> findMemberCustom();
}
