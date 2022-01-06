package study.datajpa.controller;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.controller.dto.MemberDto;
import study.datajpa.domain.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable) {
        return memberRepository.findAll(pageable).map(m -> new MemberDto(m.getId(), m.getUsername()));
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member(i, "username" + i));
        }
    }
}
