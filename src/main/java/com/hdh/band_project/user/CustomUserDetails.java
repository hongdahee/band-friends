package com.hdh.band_project.user;

import com.hdh.band_project.Authority;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {
    @Getter
    private final SiteUser user;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Authority> authorities = new HashSet<>();  // 권한 리스트를 초기화해야 함


    @ConstructorProperties({"user"})
    public CustomUserDetails(SiteUser user){
        this.user = user;
    }

    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return Collections.singletonList(new SimpleGrantedAuthority(UserRole.USER.getValue()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getUniqueId() {
        return user.getUniqueId();
    }

    public String getProfileImg() {
        return user.getProfileImg();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
