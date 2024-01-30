package com.demogroup.demoweb.service;

import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.domain.dto.UserDTO;
import com.demogroup.demoweb.exception.AppException;
import com.demogroup.demoweb.exception.ErrorCode;
import com.demogroup.demoweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    public void join(UserDTO dto) {
        System.out.println("UserService.join");

        boolean isExisted = userRepository.existsByUsername(dto.getUsername());

        if(!isExisted){
            String encodedPw = encoder.encode(dto.getPassword());
            User user = User.toEntity(dto, encodedPw);
            userRepository.save(user);
        }else{
            throw new AppException(ErrorCode.USERNAME_DUPLICATED, "이미 가입된 아이디가 존재합니다.");
        }

    }

    public UserDTO findByUsername(String username){
        //가입된 사용자 확인
        User user = userRepository.findByUsername(username)
                //Optional의 orElseThrow 함수는 람다식으로 작성해줘야 한다.
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND,"가입되지 않은 아이디입니다. 로그인을 진행합니다."));


        //가입된 사용자의 dto를 보내기
        return UserDTO.toDTO(user, user.getPassword());
    }

    //회원정보를 수정하는 메서드이다.
    //update(or delete 쿼리도 마찬가지) 쿼리를 사용하기 때문에 @Transactional 을 사용해야 한다.
    @Transactional
    public void modify(UserDTO dto,String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "가입되지 않은 아이디입니다. 로그인을 진행합니다."));
        System.out.println("UserService.modify");
        Long uid = user.getUid();
        userRepository.modifyUserData(dto.getName(), dto.getUsername(), dto.getNickname(), dto.getEmail(), uid);

    }
}
