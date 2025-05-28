package com.hdh.band_project.media;

import com.hdh.band_project.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MediaType mediaType;

    private String filePath;

    private String fileName;

    private Long fileSize;

    @ManyToOne
    private Post post;

    private LocalDateTime createdAt;

    public Media() {
    }

    public Media(Post post, MediaType mediaType, String filePath, String fileName, Long fileSize){
        this.post = post;
        this.mediaType = mediaType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.createdAt = LocalDateTime.now();
    }
}
