package com.hdh.band_project.geolocation;

import com.hdh.band_project.util.LocationCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GeoService {

    private final WebClient webClient;

    @Value("${kakao.api_key}")
    private String kakaoApiKey;

    public GeoService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("https://dapi.kakao.com").build();
    }

    public Mono<String> getAddressFromCoords(Double lat, Double lon){
        String url = String.format("/v2/local/geo/coord2address.json?x=%f&y=%f", lon, lat);

        return webClient.get()
                .uri(url)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");
                    if(documents != null && !documents.isEmpty()){
                        Map<String, Object> addressInfo = documents.get(0);
                        Map<String, Object> address = (Map<String, Object>) addressInfo.get("address");
                        String formattedAddress = (String) address.get("region_2depth_name") + ' '
                                + (String) address.get("region_3depth_name");
                        return formattedAddress;
                    }
                    return null;
                });
    }

    public Boolean checkApplyBandByDistance(Double userLat, Double userLon,
                                          Double bandLat, Double bandLon){
        double distance = LocationCalculator.calculateDistance(userLat, userLon, bandLat, bandLon);
        if(distance>30){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "가입할 수 없습니다. 밴드 위치가 30km 이상 떨어져 있습니다.");
        }
        return true;
    }
}
