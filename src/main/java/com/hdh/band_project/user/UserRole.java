package com.hdh.band_project.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    LEADER("ROLE_LEADER");

    UserRole(String value){
        this.value = value;
    }

    private String value;
}
