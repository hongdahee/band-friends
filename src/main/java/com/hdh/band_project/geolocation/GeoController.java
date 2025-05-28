package com.hdh.band_project.geolocation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/geo")
public class GeoController {

    private final GeoService geoService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/address")
    public Mono<String> getAddress(@RequestParam("lat") Double lat, @RequestParam("lon") Double lon){
        return geoService.getAddressFromCoords(lat, lon);
    }
}
