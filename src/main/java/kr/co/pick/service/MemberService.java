package kr.co.pick.service;

import kr.co.pick.dto.request.MemberReqDto;
import kr.co.pick.dto.response.MemberResDto;
import kr.co.pick.entity.Member;
import kr.co.pick.entity.Tag;
import kr.co.pick.repository.MemberRepository;
import kr.co.pick.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final TagRepository tagRepository;

    // 회원 생성
    public Long createMember(MemberReqDto requestDto) {
        isIdentity(requestDto.getIdentity());

        isTag(requestDto.getTagId());

        Member member = toEntity(requestDto);

        memberRepository.save(member);

        return member.getId();
    }

    // 아이디값으로 회원 조회
    public Member readMember(String identity) {
        Optional<Member> optionalMember = memberRepository.findByIdentity(identity);

        isMember(optionalMember);

        Member member = optionalMember.orElse(null);

        return member;
    }

    // 회원 로그인
    public MemberResDto loginMember(String identity, String password) {
        Optional<Member> member = memberRepository.findByIdentity(identity);

        isMember(member);

        isPasswordMatch(member, password);

        return toDto(member.get());
    }

    // 회원 수정
    public MemberResDto updateMember(MemberReqDto updateDto) {
        Optional<Member> member = memberRepository.findByIdentity(updateDto.getIdentity());

        isMember(member);

        Optional<Tag> updateTag = tagRepository.findById(updateDto.getTagId());

        member.get().updateMember(updateDto, updateTag.get());

        memberRepository.save(member.get());

        return toDto(member.get());
    }

    // 아이디 값으로 회원 삭제
    public void deleteMember(String identity) {
        Optional<Member> member = memberRepository.findByIdentity(identity);

        isMember(member);

        memberRepository.delete(member.get());
    }

    // 해당 멤버 존재 체크
    private void  isMember(Optional<Member> member) {
        if (member.isEmpty()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }
    }

    // 회원의 identity 값 중복 체크
    private void isIdentity(String identity) {
        // id 값이 이미 db에 존재하는 값이면 true
        if (memberRepository.findByIdentity(identity).isPresent()) {
            throw new RuntimeException("아이디 값이 중복되었습니다.");
        }
    }

    private void isTag(Long tagId) {
        // id 값이 이미 db에 존재하는 값이면 true
        if (tagRepository.findById(tagId).isEmpty()) {
            throw new RuntimeException("존재하지 않는 태그입니다.");
        }
    }

    // 비밀번호 매칭 체크
    private void isPasswordMatch(Optional<Member> loginMember, String password) {
        if (!loginMember.get().getPassword().equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }

    private Member toEntity(MemberReqDto dto) {
        return Member.builder()
                .identity(dto.getIdentity())
                .password(dto.getPassword())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .tag(tagRepository.findById(dto.getTagId()).get())
                .build();
    }

    private MemberResDto toDto(Member member) {
        return MemberResDto.builder()
                .memberId(member.getId())
                .identity(member.getIdentity())
                .password(member.getPassword())
                .name(member.getName())
                .nickname(member.getNickname())
                .birthDate(member.getBirthDate())
                .gender(member.getGender())
                .tagId(member.getTag().getId())
                .memberPcolor(member.getTag().getPcolor())
                .memberSkinColor(member.getTag().getSkinColor())
                .memberSkinType(member.getTag().getSkinType())
                .build();
    }
}
