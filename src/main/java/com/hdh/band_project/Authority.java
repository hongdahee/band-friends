package com.hdh.band_project;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Embeddable
public class Authority implements GrantedAuthority {

    private String role;

    public Authority() {}

    public Authority(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

}
