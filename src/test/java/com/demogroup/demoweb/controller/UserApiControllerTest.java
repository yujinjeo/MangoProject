package com.demogroup.demoweb.controller;

import com.demogroup.demoweb.domain.dto.UserDTO;
import com.demogroup.demoweb.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.ServerResponse.status;

@WebMvcTest
//@SpringBootTest
//@AutoConfigureMockMvc
class UserApiControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
//    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void join() throws Exception{

        mockMvc.perform(post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserDTO(1l, "정유진","rocketdan01","aabbcd01","12ae567%","abcdqwer@ewhain.net","USER"))))
                .andDo(print())
                .andExpect(status().isOk());
    }


}