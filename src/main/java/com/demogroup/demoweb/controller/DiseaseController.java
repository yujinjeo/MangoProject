package com.demogroup.demoweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/disease")
public class DiseaseController {

    //질병 식별 시작 페이지 화면을 반환하는 컨트롤러 입니다.
    @GetMapping("/home")
    public String homePage(){
        return "disease/home";
    }
}
