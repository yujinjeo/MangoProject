package com.demogroup.demoweb.security;

import com.demogroup.demoweb.domain.CustomUserDetails;
import com.demogroup.demoweb.exception.AppException;
import com.demogroup.demoweb.exception.ErrorCode;
import com.demogroup.demoweb.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;

@Slf4j
//@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;

        //직접 UsernamePasswordAuthenticationFilter를 custom해주는 것이기 때문에
        //HttpSecurity에서 formLogin으로 설정해도 안되고
        //이렇게 setFilterProcessesUrl로 설정해줘야 된다.
        setFilterProcessesUrl("/api/user/login");
    }

    //request로부터 아이디와 비밀번호를 받아서
    //usernamepasswordauthenticationtoken에 넣어
    //authenticationmanager에 전달하여 인증을 진행시키는 메소드이다.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("LoginFilter.attemptAuthentication");

        String username = super.obtainUsername(request);
        String password = super.obtainPassword(request);

        System.out.println(username);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(username,password,null);

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        System.out.println("successful login");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String role = ((CustomUserDetails) authentication.getPrincipal())
                .getAuthorities()
                .iterator().next()
                .getAuthority();

        //1초*60(분)*60(1시간)=>1시간 기한의 JWT이다.
        String token = jwtUtils.createToken(customUserDetails,  60 * 60 * 1000L);
        response.addHeader("Authorization","Bearer "+token);
        System.out.println("이 글자가 보이면 response jwt 성공");

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("login fail");
//        throw new AppException(EorCode.LOGIN_FAILED,"사용자가 존재하지 않습니다");
    }
}
