package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.domain.CustomUserDetails;
import com.demogroup.demoweb.domain.dto.UserDTO;
import com.demogroup.demoweb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //사용자 회원가입 페이지
    @GetMapping("/join")
    public String joinPage(){
        return "join";
    }

    //사용자 회원가입 수행
    @PostMapping("/joinProc")
    public String join(@Valid UserDTO dto){
        userService.join(dto);
        return "redirect:/loginP";
    }

    //사용자 로그인 페이지
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    //회원정보 수정 화면
    @GetMapping("/modify")
    public String modifyPage(Model model){
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("name",principal.getName());
        model.addAttribute("username",principal.getUsername());
        model.addAttribute("nickname",principal.getNickname());
        model.addAttribute("password",principal.getPassword());
        model.addAttribute("email",principal.getEmail());
        System.out.println(principal.getUsername());
        return "modify";
    }

}
