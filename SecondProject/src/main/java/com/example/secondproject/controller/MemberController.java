package com.example.secondproject.controller;

import com.example.secondproject.domain.user.Member;
import com.example.secondproject.dto.RegisterForm;
import com.example.secondproject.dto.paging.MemberDto;
import com.example.secondproject.dto.paging.MemberPageDto;
import com.example.secondproject.repository.MemberRepository;
import com.example.secondproject.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    //회원가입
    @GetMapping("/register")
    public String joinForm(@ModelAttribute RegisterForm registerForm) {
        return "users/register";
    }

    @PostMapping("/register")
    public String join(@ModelAttribute @Validated RegisterForm registerForm,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "users/register";
        }

        Member newMember = new Member(registerForm.getLoginid(), registerForm.getName(),
                registerForm.getPassword(), registerForm.getEmail(), "MEMBER");
//        newMember.setLoginid(registerForm.getLoginid());
//        newMember.setName(registerForm.getName());
//        newMember.setPassword(registerForm.getPassword());
//        newMember.setEmail(registerForm.getEmail());

        memberService.createUser(newMember);

        return "redirect:/";
    }

    //로그인
    @GetMapping("/login")
    public String memberLogin() {
        return "users/loginForm";
    }

    @GetMapping("/login/denied")
    public String memberLoginDenied() {
        return "/users/denied";
    }

    @GetMapping("/logout")
    public String memberLogout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/";
    }


///////////////////////////////admin/////////////////////

    @GetMapping("/admin/users/{memberId}")
    public String detailOfUserByAdmin(@PathVariable("memberId") Long id, Model model) {

        Member member = memberService.findOneById(id);

        model.addAttribute("memberForm", member);

        return "users/admin/userDetailByAdmin";
    }

    /*
    회원 추방
    */
    @DeleteMapping("/admin/users/delete/{memberId}")
    public String deleteForm(@PathVariable("memberId") Long memberId) {
        log.info("BoardController DeleteMapping deleteForm");
        memberService.deleteMember(memberId);
        return "redirect:/admin/users";
    }

    /*
    Paging 회원 목록
    * */
    @GetMapping("/admin/users")
    public String userList(Model model,
                           @PageableDefault(size = 20, sort = "id",
                                   direction = Sort.Direction.ASC) Pageable pageable) {
        Page<MemberDto> results = memberRepository.findAllPageSort(pageable);

        model.addAttribute("users", results.getContent());
        model.addAttribute("page", new MemberPageDto(results.getTotalElements(), pageable));

        return "users/admin/pagingMemberList";
    }

    /*
     * 멤버 수정 admin*/
    @GetMapping("/admin/users/edit/{memberId}")
    public String updateUserForm(@PathVariable("memberId") Long memberId,
                                 Model model) {

        Member findOne = memberService.findOneById(memberId);

        MemberDto result = new MemberDto(memberId, findOne.getName(), findOne.getLoginid(),
                findOne.getEmail(), findOne.getRole());

        model.addAttribute("memberDto", result);
        model.addAttribute("roleTypes", RoleTypes.values());

        return "users/admin/updateUserByAdmin";
    }


    @PostMapping("/admin/users/edit/{memberId}")
    public String updateUserByAdmin(@PathVariable("memberId") Long memberId,
                                    @ModelAttribute("memberDto") MemberDto memberDto) {

        memberService.updateByAdmin(memberId, memberDto.getName(), memberDto.getLoginid(),
                memberDto.getEmail(), memberDto.getRole());

        return "redirect:/admin/users";
    }

//
//    @ModelAttribute("roleTypes")
//    public RoleTypes[] roleTypes(){
//        return RoleTypes.values();
//    }
}
