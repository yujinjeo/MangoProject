package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.domain.CustomUserDetails;
import com.demogroup.demoweb.domain.dto.UserDTO;
import com.demogroup.demoweb.service.UserService;
import com.demogroup.demoweb.utils.MakeJsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

//@Controller
//@RequiredArgsConstructor
//public class tempController {
//
//    private final UserService userService;
//
//    @PostMapping("/modify")
//    public String modify(String token){
//        return "modify";
//
//    }
//
//    @GetMapping("/loginform")
//    public String loginPage(){
//        return "login";
//    }
//
//    @GetMapping("/join")
//    public String joinPage(){
//        return "join";
//    }
//
//    @PostMapping("/joinProc")
//    public String joinProcess(@Valid UserDTO dto){
//        System.out.println(dto.getUid());
//        System.out.println(dto.getName());
//        userService.join(dto);
//        return "redirect:/loginform";
//    }
//
//    @GetMapping("/admin")
//    public String adminPage(Model model){
//        System.out.println("tempController.adminPage");
//        String name = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        System.out.println(name);
//        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        model.addAttribute("nickname",userDetails.getNickname() );
//        return "admin";
//    }
//
//    @GetMapping("/error")
//    public String errorPage(){
//        return "error";
//    }
//
//    @GetMapping("/logout")
//    public String logoutProc(HttpServletRequest request, HttpServletResponse response) throws Exception{
//        System.out.println("tempController.logoutProc");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication!=null){
//            new SecurityContextLogoutHandler().logout(request,response,authentication);
//        }
//        return "redirect:/";
//    }
//}
