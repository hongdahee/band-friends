package com.hdh.band_project.media;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.security.Principal;

import static com.hdh.band_project.media.MediaService.UPLOAD_PATH;

@Controller
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/play/{filename}")
    public ResponseEntity<Resource> playFile(@PathVariable("filename") String filename) throws MalformedURLException {
        Path file = UPLOAD_PATH.resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/media/delete/{mediaId}")
    public ResponseEntity<?> delete(@PathVariable("mediaId") Long mediaId, Principal principal) {
        mediaService.deleteMedia(mediaId, principal.getName());
        return ResponseEntity.ok().build();
    }
}
