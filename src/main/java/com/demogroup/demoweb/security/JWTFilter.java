package com.demogroup.demoweb.security;

import com.demogroup.demoweb.domain.CustomUserDetails;
import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.domain.dto.UserDTO;
import com.demogroup.demoweb.exception.AppException;
import com.demogroup.demoweb.exception.ErrorCode;
import com.demogroup.demoweb.repository.UserRepository;
import com.demogroup.demoweb.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    private final JWTUtils jwtUtils;
    private final RedisTemplate<String ,String> redisTemplate;

    //로그아웃된 JWT 토큰을 구별하는 곳입니다.
    //"Bearer "그대로 저장

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //헤더에서 토큰 가져오기
        String token0 = request.getHeader("Authorization");

        //토큰 유무 확인
        if(token0==null || !token0.startsWith("Bearer ")){
            System.out.println("토큰이 없습니다. 로그인을 진행합니다");
            filterChain.doFilter(request,response);
            return;
        }

        //순수 토큰 획득
        String token = token0.split(" ")[1];

        //시간 만료 여부 검증
        if(jwtUtils.isExpired(token)){
            System.out.println("토큰 기간이 만료되었습니다. 로그인을 진행합니다");
            filterChain.doFilter(request,response);
            return;
        }

        //redis blacklist에 있는지 확인
        String username_ = jwtUtils.getUsername(token);
        Boolean isListed = redisTemplate.hasKey(token);
        if(isListed){
            System.out.println("로그아웃되었습니다. 다시 로그인을 진행합니다");
            String jwt = redisTemplate.opsForValue().get(token);
            filterChain.doFilter(request,response);
            return;
        }

        //******스프링 시큐리티 context에 등록하기******
        String name = jwtUtils.getName(token);
        String username = jwtUtils.getUsername(token);
        String nickname = jwtUtils.getNickname(token);
        String email = jwtUtils.getEmail(token);
        String role = jwtUtils.getRole(token);

        //임시 비밀번호, repository에서 가져와야한다.
        User byUsername = userRepository.findByUsername(username)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND,"가입되지 않은 아이디입니다. 로그인을 진행합니다."));
        String password = byUsername.getPassword();

        //User 엔티티 생성
        UserDTO dto=new UserDTO(name,username,nickname,email,role);
        User user=User.toEntity(dto,password);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println("JWTFilter.doFilterInternal");

        filterChain.doFilter(request,response);

    }
}
