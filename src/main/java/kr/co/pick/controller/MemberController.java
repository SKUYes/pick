package kr.co.pick.controller;

import jakarta.servlet.http.HttpSession;
import kr.co.pick.dto.request.MemberReqDto;
import kr.co.pick.dto.response.MemberResDto;
import kr.co.pick.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public void join() {

    }

    @PostMapping("/join")
    public String join(@ModelAttribute MemberReqDto memberReqDto, RedirectAttributes redirectAttributes) {

        memberService.createMember(memberReqDto);

        String memberIdentity = memberReqDto.getIdentity();

        redirectAttributes.addFlashAttribute("memberIdentity", memberIdentity);

        return "redirect:/home";
    }

    @GetMapping("/login")
    public void login() {

    }

    @PostMapping("/login")
    public String login(@ModelAttribute MemberReqDto memberReqDto, HttpSession session) {

        MemberResDto loginMember = memberService.loginMember(memberReqDto.getIdentity(), memberReqDto.getPassword());
        session.setAttribute("loginMember", loginMember);

        return "redirect:/home";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loginMember");

        return "redirect:/home";
    }

    @GetMapping("/update")
    public void update() {

    }

    @PostMapping("/update")
    public String update(@ModelAttribute MemberReqDto updateReqDto, HttpSession session) {
        MemberResDto result = memberService.updateMember(updateReqDto);

        session.removeAttribute("loginMember");

        session.setAttribute("loginMember", result);

        return "redirect:/home";
    }

    @GetMapping("/delete")
    public String delete(@SessionAttribute("loginMember") MemberResDto loginMember, HttpSession session){
        memberService.deleteMember(loginMember.getMemberId());

        session.removeAttribute("loginMember");

        return "/home";
    }

}
