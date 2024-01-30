package com.demogroup.demoweb.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_USER("ROLE_USER");

    private String roleName;
}
