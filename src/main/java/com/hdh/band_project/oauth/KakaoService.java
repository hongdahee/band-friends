package com.hdh.band_project.oauth;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hdh.band_project.user.CustomUserDetails;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

@Getter
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserService userService;

    @Value("${kakao.api_key}")
    private String kakaoApiKey;

    @Value("${kakao.redirect_uri}")
    private String kakaoRedriectUri;


    public String getAccessToken(String code) {
        String accessToken = "";
        String refreshToken = "";
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        try{
            RestTemplate rt = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", kakaoApiKey);
            params.add("redirect_uri", kakaoRedriectUri);
            params.add("code", code);

            HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = rt.exchange(reqUrl, HttpMethod.POST,
                    kakaoTokenRequest, String.class);

            String responseBody = response.getBody();

            Gson gson = new Gson();
            KakaoToken kakaoToken = gson.fromJson(responseBody, KakaoToken.class);
            accessToken = kakaoToken.getAccess_token();

        }
//        catch (UnknownHostException e) {
////            log.error("Kakao API 서버에 연결할 수 없습니다: " + e.getMessage());
//            // 사용자에게 네트워크 문제로 연결이 불가능하다는 메시지 반환
//            return "네트워크 연결이 불안정합니다. 잠시 후 다시 시도해주세요.";
//        }
        catch (Exception e) {
//            log.error("알 수 없는 오류 발생: " + e.getMessage(), e);
//            return "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
            e.printStackTrace();
        }
        return accessToken;
    }

    public String getUserEmail(String accessToken){
        String userEmail = "";
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        try{
            RestTemplate rt = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            HttpEntity<MultiValueMap<String, String>> kakaoEmailRequest = new HttpEntity<>(headers);

            ResponseEntity<String> response = rt.exchange(reqUrl, HttpMethod.POST, kakaoEmailRequest,
                    String.class);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(response.getBody());

            JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            userEmail = kakaoAccount.getAsJsonObject().get("email").getAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return userEmail;
    }
}
