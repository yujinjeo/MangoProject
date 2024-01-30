package com.demogroup.demoweb.service;

import com.demogroup.demoweb.domain.CustomOAuth2User;
import com.demogroup.demoweb.domain.OAuth2Response;
import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.domain.dto.NaverResponse;
import com.demogroup.demoweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>  {
    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
        //OAuth2User 정보를 가져옵니다.
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        //제공자 등록 id('naver', 'google')를 가져오고, oAuth2User에서 속성들을 가져올 수 있게 변환합니다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response=null;
        if(registrationId.equals("naver")){
            //json을 OAuth2Response라는 객체의 Map 클래스를 사용하여 반환받습니다.
            oAuth2Response=new NaverResponse(oAuth2User.getAttributes());
            System.out.println(oAuth2Response.getEmail());

            String email = oAuth2Response.getEmail();
            Optional<User> byEmail = userRepository.findByEmail(email);
            //가입되어 있지 않은 사용자인 경우 회원가입 페이지로 리디렉션한다.
            if (byEmail.isEmpty()){
                return new CustomOAuth2User(oAuth2Response,"ROLE_USER",false);
            }
        }
        else {
            throw new RuntimeException();
        }
        String role="ROLE_USER";
        System.out.println("CustomOAuth2UserService.loadUser");

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2Response, role, true);
        return customOAuth2User;
    }
}
