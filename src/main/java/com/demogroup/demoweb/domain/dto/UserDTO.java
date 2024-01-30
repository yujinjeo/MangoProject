package com.demogroup.demoweb.domain.dto;

import com.demogroup.demoweb.domain.User;
import com.demogroup.demoweb.utils.annotation.PasswordValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDTO {
    private Long uid;
    private String name;
    private String nickname;
    private String username;

    //@Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=.*\\S+$).{8,16}",message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    @PasswordValid(message = "비밀번호는 영문자(대소문자 가능),숫자,특수문자를 포함한 8~16자 이여야 합니다.")
    private String password;
    private String email;

    //'USER'
    private String role;
//    private LocalDateTime createDate;
//    private LocalDateTime modifiedDate;

    public UserDTO(String name, String username,String nickname, String email, String role){
        this.name=name;
        this.username=username;
        this.nickname=nickname;
        this.email=email;
        this.role=role;
    }

    public static UserDTO toDTO(User user,String password){
        return UserDTO.builder()
                .uid(user.getUid())
                .name(user.getName())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .password(password)
                .email(user.getEmail())
                .role(user.getRole())
                .build();

    }

}
