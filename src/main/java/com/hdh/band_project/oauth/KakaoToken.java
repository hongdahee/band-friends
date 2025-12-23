package com.hdh.band_project.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Integer expires_in;
    private Integer refresh_token_expires_in;
}
