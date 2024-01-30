package com.demogroup.demoweb.config;

import com.demogroup.demoweb.domain.CustomOAuth2User;
import com.demogroup.demoweb.domain.CustomUserDetails;
import com.demogroup.demoweb.domain.Role;
import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.exception.AppException;
import com.demogroup.demoweb.exception.ErrorCode;
import com.demogroup.demoweb.repository.UserRepository;
import com.demogroup.demoweb.security.*;
import com.demogroup.demoweb.service.CustomOAuth2UserService;
import com.demogroup.demoweb.utils.JWTUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.util.Collection;

//@Configuration을 사용하여, SpringContext에서 bean리스트를 만들고, 이후 FilterChainProxy에서 빈들을 찾을 수 있도록 bean을 생성하는 클래스임을 명시한다.
@Configuration
//FilterChainProxy에서 SecurityFilterChain을 찾고, @EnableWebSecurity로 표시하여 filterchain이 동작할 수 있도록 한다.
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
//final 키워드가 붙은 private 변수에 대해 빈을 가져와 생성자를 주입한다.
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    private final RedisTemplate<String,String> redisTemplate;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtils jwtUtils;
    private final UserRepository userRepository;
    private final CustomOAuth2UserService customOAuth2UserService;


    /*로그인 인증 시 인증을 실질적으로 담당하는 인터페이스인 AuthenticationManager를 생성하는 Bean이다.
    AuthenticationConfiguration을 사용하여 AuthenticationManager를 얻는데, AuthenticationConfiguration은 스프링에서 제공하는 객체를 생성자주입받으면 된다.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
    SecurityFilterChain은 실질적으로 'custom'하는 보안 필터라고 생각할 수 있다. 이 bean은 SecurityFilterChain을 반환하는데,
    HttpSecurity는 AbstractConfiguredSecurityBuilder<SecurityFilterChain, HttpSecurity>를 상속받고 있기 때문에
    build() 하면 SecurityFilterChain 객체를 생성할 수 있다.
    HttpSecurity는 SecurityFilterChain을 만들 때 인증, 인가, csrf, cors 등 다양한 설정을 할 수 있도록 API를 제공하는 객체이다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf->csrf
                        .disable()
                ) // JWT를 사용하여 인증하기 때문에 csrf 보호를 해제한다.
//                .formLogin((form)->form
//                        .disable()
//                )
                .httpBasic((basic)->basic
                        .disable()
                ) // httpBasic 로그인 방식도 사용하지 않는다.
                .authorizeHttpRequests(request->request
                        .requestMatchers("/user/**","/api/**","/home","/disease/**","/api/disease/**").permitAll()
                        .requestMatchers("/","/login/**","/oauth2/**","/loginform","/join","/joinProc").permitAll()
                        .requestMatchers("/admin","/user/modify","/api/user/modify").hasRole("USER")
                        .anyRequest().authenticated()
                ) /* 해당 사용자에 따라 제공하는 자원에 차이를 두는 '인가'를 설정한다.
                 AuthorizationFilter는 security filter 중 거의 마지막에 위치하는데, 인증을 받은 사용자들을 -> 인가하는, 순서를 지키기 위해서이다.
                 SecurityContext에서 사용자가 가진 Role을 기준으로 여러 권한을 결정한다.
                - permitAll(): 누구나 해당 자원에 접근할 수 있는(요청할 수 있는) permitAll()가 있다. 여기에는 로그인 관련 urn이나 인증이 필요하지 않은 모든 접근가능한 자원을 명시한다.
                - .hasRole() : Security context에 등록되어 있는 Authentication에서 ROLE 값을 확인한다. 스프링 시큐리티에서는 ROLE을 "ROLE_"이라는 접두사를 붙여
                관리하는데, hasRole 함수 사용 시에는 "USER"와 같은 권한 명만 붙인다. ROLE_USER 인 권한을 가진 접근자만이 "/admin" 자원을 이용할 수 있다는 말이다.
                */
                .addFilterBefore(new JWTFilter(userRepository,jwtUtils,redisTemplate), LoginFilter.class
                ) /* LoginFilter 전에 유효한 jwt를 http request header의 "Authorization"에 보냈는지 확인하고 매 HTTP 요청마다 (일시적으로)
                Security context에 authentication을 만들어 넣어주는 필터를 추가했다.
                나는 session 방식이 아니라 jwt 방식을 사용하기 때문에, stateless http로 요청하는 사용자가 전에 인증된 사용자인지 알 수 없다.
                JWTFilter를 통해 매 요청마다 진행되는 일회성의 authentication을 통해 사용자 전용 마이페이지 등을 보여주는 방식을 취한다.

                && session과 JWT의 차이 :
                - Session : 세션은 로그인한 사용자에게 security context에 저장된 authentication을 구분하는 인덱스인 SESSIONID를 발급한다.
                사용자가 SESSIONID를 가져올 때, 서버에서는 해당 SESSIONID에 해당하는 authentication이 security context에 있는지 확인하는 방식을 취한다.
                즉, security context가 로그인한 사용자들의 authentication 객체들을 계속 들고 있는거다.
                이는 안전한 방식이고, 서버 저장공간이 부족해진다는 장단점을 가지고 있다.
                - JWT : json web token의 약자로, JWT 내에 사용자 정보를 담은 것이다.
                로그인한 사용자에게 JWT 문자열을 발급하고 사용자가 다음 Http 요청 때 jwt를 들고 오면
                서버의 secret key와 일치하는지 비교하고 JWT를 열어(?)서 사용자의 username이라던지, role이라던지, email이라던지 하는 정보들을 꺼낼 수 있다.

                */
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtUtils)
                        ,UsernamePasswordAuthenticationFilter.class
                )
                /*UsernamePasswordAuthenticationFilter는 기본적으로 session 저장 방식이다. 필터를 들어가서 확인해보면,
                AbstractAuthenticationProcessingFilter를 상속받고 있고, 그 객체에 successfulAuthentication 함수가 있는데, 여기서 default로 정의한
                방식은 Security context에 인증된 사용자 정보를 넣어 세션을 만드는 방식이다.
                한마디로, formLogin과 같이 그냥 UsernamePasswordAuthenticationFilter를 사용하면, session 방식으로 진행되기 때문에,
                jwt 방식을 사용하기 위해 LoginFilter라는 UsernamePasswordAuthenticationFilter를 상속하여 custom(?)한 로그인 필터를 만들어서,
                addFilterAt이라는 함수를 통해 원래 UsernamePasswordAuthenticationFilter가 있었던 자리에 내가 만든 LoginFilter를 갈아끼웠다.
                LoginFitler에서는 Security context에 저장하지 않고, response 객체에 JWT를 담아 사용자에게 전송한다.
                */
                .logout((out)->out
                        .logoutUrl("/user/logout")
                        .permitAll()
                        .addLogoutHandler(new LogoutHandler(){
                            @Override
                            public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

                                //JWT를 블랙리스트에 올린다.
                                String token_ = request.getHeader("Authorization");
                                String token=token_.split(" ")[1];
                                String username = jwtUtils.getUsername(token);
                                String logout = jwtUtils.logout(token);
                            }
                        })
                        .logoutSuccessHandler(new LogoutSuccessHandler() {
                            @Override
                            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                response.sendRedirect("/user/login");
                            }
                        })
                )/*
                로그아웃 필터는 필터체인의 거의 앞쪽에 위치해 있는 필터이다.
                나는 JWT를 사용하고 있기 때문에 Security context에서 세션 아이디를 찾아 삭제하고 context를 clear하는 방식을 사용하지 않기 때문에,
                logoutfilter의 기본 logout, onLogoutSuccess 함수를 사용하지 않고 custom해서 사용한다.
                JWT를 블랙리스트에 올리는 방식이다. 아직 Redis를 연동하지 못했지만 Redis cache에 해당 JWT를 올리고 JWTFilter에서
                Redis cache 포함 여부를 확인해 로그아웃을 진행한다.
                Redis cache에 올려도 토큰 기한이 만료될 때까지 기다려야 한다. 이렇게 번거롭게 작업하는 이유는, 인증의 주체가 서버가 아니라
                사용자에게 있기 때문에, 로그아웃을 하기 위해서는 서버에서 해당 JWT를 막는(블랙리스트에 올리는) 방법을 사용한다.
                세션은 다르다. 세션은 인증 주체가 서버이기 때문에 서버에서 SESSION을 삭제해버리면 사용자가 SESSIONID를 들고와도 인증이 불가하다.

                logout에서는 다음과 같은 함수들을 사용할 수 있다.
                - logoutUrl() : 로그아웃하는 url(string 값)을 지정하는 함수이다. 기본적으로 "/logout"으로 지정되어 있다.
                - logoutSuccessUrl() : 로그아웃 성공 시 어느 url로 이동할지 지정하는 함수이다.
                - addLogoutHandler() : JWT 사용과 같이, 로그아웃을 진행하는 과정에 대한 본인의 정의가 필요할 때, 이 함수 안에 LogoutHandler
                객체를 생성하여 넣을 수 있다. 나는 익명 객체로 넣어주었다.
                - logoutSuccessHandler() : 로그아웃 성공 시 특정 url로 리디렉션만 하는 것이 아니라 추가적인 설정이 필요할 때 이 함수 안에
                LogoutSuccessHandler 객체를 생성하여 넣을 수 있다.
                 */
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )/* JWT를 사용하기 때문에 사용자 정보를 session에 지속적으로 저장하지 않는다.
                SessionCreationPolicy 를 뜯어보면 다음과 같은 4가지 상태가 있음을 확인할 수 있다.
                - ALWAYS : 매 HTTP 요청이 들어올 때마다 새로운 HttpSession을 생성하는 것이다.
                - NEVER : HttpSession을 더 이상 생성하지 않는다. 그러나 이미 존재하는 HttpSession은 사용한다.
                - IF_REQUIRED : default 값이다. 사용자가 필요하다면 session을 생성할 수 있다. 보통의 session 생성 후 security context 에 저장하는 과정이 이 policy 안에서 이루어진다.
                - STATELESS : HttpSession을 사용하지 않고, SecurityContext에서도 사용하지 않는다는 뜻이다. JWT와 같이 세션을 사용하지
                않는 방식에서 이 Policy를 적용한다.
                */
                .oauth2Login((oauth2)->oauth2
                        .loginPage("/user/login")
                        .userInfoEndpoint((userInfoEndpointConfig)->userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(oauthSuccessHandler())
                )/* 하나 만드느라 진땀을 뺐던 oauth2이다. (다른 사람들은 쉽게쉽게 하는데 왜 나만 어려운지.. ㅠㅜ)
                아직 잘 이해하지 못했지만, 이해한 대로만 작성해보자면,
                소셜 로그인은 외부 사이트에서 로그인할 수 있는 방식이다. 외부 사이트에서 로그인을 했다면, 내 서버(이 프로젝트) 에서도 사용자를
                승인하고(OAuth2UserDetails에서 가입 여부 확인도 한다) 이 사이트에 접속할 수 있게 JWT를 발급하는 것이다. (SESSION을 사용한다면
                사용자의 SESSION 정보를 Security context에 저장하는 방식으로)
                그러니 내 사용자를 외부 로그인 서버로 보내주고, 외부 로그인 서버에서 사용자의 성공 여부와 각종 정보들을 보내주는 과정이 있어야 하는 것이다.

                && 일반적인 폼 로그인 방식의 로그인 로직 :
                일반적인 폼 로그인 방식에서는 /login 으로 온 요청을 가로채고 검증 객체에 보내주고 성공/ 실패 후 로직을 담당하는
                UsernamePasswordAuthenticationFilter 가 request를 먼저 받는다.
                이 filter가 요청한 AuthenticationManager(.authenticate()메소드를 통해)에게 사용자 아이디와 비밀번호를 담은 UsernamePasswordAuthenticationToken이라는
                객체에 담아 실제 사용자 인증을 진행하는 과정을 맡기면,
                이 AuthenticationManager는 AuthenticationProvider들을 통해 인증을 지시한다. (왜 여러 provider를 두는지는 잘 모르겠다.)
                이 AuthenticationProvider들이 하는 일이란, username을 통해 db에 접근하여 가입된 사용자인지를 확인하는, 그러니까 실질적인 db 확인을 수행하는
                UserDetailsService에게서(.loadByUsername이라는 함수를 통해) null 또는 UserDetails 객체를 받고 UserDetails 객체를 잘
                받았다면 Security Context에 넣을 수 있는 Authentication 객체를 생성하여 UsernamePasswordAuthenticationFilter의 성공 메소드에게 전달하고
                null 값을 받았다면 AuthenticationException 을 발생시켜 UsernamePasswordAuthenticationFilter의 실패 메소드를 불러 처리하는 방식이라고 할 수 있다.
                그러니 AuthenticationProvider는 성공인지, 실패인지, 성공이라면 해당 Authentication 객체까지 생성해서 바로 Security Context에 넣을 수
                있게 해주는 그런 객체라고 생각할 수 있겠다. UserDetails와 UserDetailsService는, User라는 객체를 담고, 나중에 authentication을 꺼낼 때
                UserDetails가 안에 있는데,(.getPrincipals()) 여기서 userDetails 객체에 대한 다양한 정보를 꺼낼 수 있도록/ 그리고 DB에 연결해서 UserDetails를 반환할
                수 있도록 Custom 해서 작성해줘야 사용하고자 원하는 의도대로 사용할 수 있다.

                && OAuth2 방식 로그인 로직 :
                이제 oauth2 방식을 이야기해보겠다. 외부 소셜로그인 서버와 소통한다는 것 말고는 로직이 비슷해서 비슷하게 이해해볼 수 있다.
                - OAuth2AuthenticationRedirectFilter : 먼저 소셜로그인을 하고자 하는 사용자 요청(일반적으로 /oauth2/authorization/naver)이 들어왔다면 외부 로그인 서버에서 로그인을 진행하도록 보내줘야 한다.
                이것은 요청을 가로채고, 외부 로그인서버에서 등록한 '내 프로젝트'의 client id와 secret key 등의 정보와 함께,
                외부 '인증서버' 로 갈 수 있도록 리다이렉션 시켜주는 OAuth2AuthorizationRequestRedirectFilter 에서 작업을 한다.
                '인증서버'로 리다이렉션 해준다는 의미를 직관적으로 담고 있는 클래스명이다.
                - OAuth2LoginAuthenticationFilter : '인증 서버'에서 로그인이 잘 진행되었다면 외부 '인증 서버'가 내 서버로 code를 담아 '로그인이 잘 되었다' 라는 답변을 한다.
                이때 인증서버가 답변을 할 때 내 서버의 어느 urn으로 답변을 해야하는지는 소셜로그인 프로젝트 등록할 때 적어줘야 한다.
                이는 "/login/oauth2/code/naver" 라는 urn으로 온다. 이 요청은 OAuthLoginAuthenticationFilter가 가로챈다. 이 필터는
                해당 요청을 가로채고, 인증 서버에게서 받은 code를 다시 인증서버에게 보내서 '리소스 서버(사용자 정보가 있는 서버)'에 접근하여
                사용자 정보를 받아올 수 있는 Access Token을 발급받아 OAuth2AuthenticationProvider에게 전달해주는 역할을 수행한다.
                Access Token이 필요한 이유는 naver나 google의 인증 서버와 리소스 서버가 (당연하게도) 다르기 때문이다. 리소스 서버에는
                함부로 접근할 수 없을 것이다. 보안이 있는 리소스서버에게 접근할 수 있도록 인증 서버가 Access Token을 발급해주는 것이다.
                - OAuthAuthenticationProvider : 'AuthenticationProvider'! 어디서 들어보지 않았는가? 맞다! 일반적인 폼 로그인 방식에서
                UserDetailsService에게서 UserDetails 를 받고 인증 객체 혹은 AuthenticationException을 발생시키는, 실질적인 인증을 담당하는 객체다.
                여기서도 OAuth2AuthenticationProvider 객체는 사용자 정보를 받고, OAuth2UserService 에게서 OAuth2User 객체(폼 로그인의
                UserDetails에 해당한다)를 반환받고, Authentication을 구현하는  <<OAuth2LoginAuthenticationToken>> 을 만들어서,
                OAuth2AuthenticationFilter에게 전달한다. OAuth2AuthenticationFilter는 UsernamePasswordAuthenticationFilter와
                같이, <<<AbstractAuthenticationProcessingFilter>>>를 상속받는다. 이 abstractfilter에는 successfulAuthentication 메소드와
                unsuccessfulAuthentication 메소드가 있기 때문에, provider에서 Authentication 객체를 보내주면, 혹은 AuthenticationException을
                발생시키면 이 메소드들에서 최종적인, 인증 성공, 실패 이후 로직을 결정하는 것이다.
                */
                .build();
    }

    /*
    * oauth2Login 메소드에 대하여, OAuth2LoginAuthenticationFilter를 교체하여 successfulAuthentication 메소드를 오버라이딩 하는 것은
    * 혹시나 다른 오류가 생길지 몰라 시도하지 않고,
    * 이렇게 api를 사용해서 successfulHandler() 함수에서 JWT 발급을 처리하도록 만들었다.
    * 소셜로그인이 성공적으로 완료되면, 이 oauthSuccessHandler에서 authentication을 받아온다. 이는 OAuth2AuthenticationProvider가
    * OAuth2LoginAuthenticationToken이라는 Authentication을 구현한 객체이다.
    * authentication에서 getPrincipal로 UserDetails와 비슷한 OAuthUser객체를 꺼내고 다시 이메일을 꺼내(가입된 이메일이 있는지 없는지는
    * CustomOAuth2UserService에서 진행했다.) db에서 해당 이메일을 가진 user 객체를 꺼내고(이 부분은 수정이 필요할 듯 하다)
    * JWT를 생성하여 response에 보내는 역할까지 수행한다.
    * */
    @Bean
    public AuthenticationSuccessHandler oauthSuccessHandler(){
        return (request, response, authentication) -> {
            //OAuth2AthenticationFilter에서 저장해줬던 세션을 삭제한다
            HttpSession session = request.getSession(false);
            SecurityContextHolder.clearContext();
            if(session!=null){
                session.invalidate();
            }
            System.out.println(SecurityContextHolder.getContext());

            //CustomOAuth2User 가져오기
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getEmail();
            User user=userRepository.findByEmail(email)
                    .orElseThrow();
            System.out.println("====================================");
            System.out.println(user.getUsername());
            CustomUserDetails dto = new CustomUserDetails(user);
            String oAuth2Token = jwtUtils.createToken(dto, 60 * 60 * 1000L);
            System.out.println(oAuth2Token);
            response.addHeader("Authorization","Bearer "+oAuth2Token);
        };
    }
}
