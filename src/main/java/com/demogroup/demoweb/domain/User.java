package com.demogroup.demoweb.domain;

import com.demogroup.demoweb.domain.dto.UserDTO;
//import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    private String name;
    private String nickname;
    //로그인 시 사용할 id입니다
    @Column(unique = true)
    private String username;

    private String password;
    private String email;

    private String role=Role.ROLE_USER.name();

    public void updateUsername(String username){
        this.username=username;
    }

    public static User toEntity(UserDTO dto, String encodePw){
        return User.builder()
//                .uid(dto.getUid())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .username(dto.getUsername())
                .password(encodePw)
                .email(dto.getEmail())
                .role(Role.ROLE_USER.name())
                .build();

    }
}
