package com.hdh.band_project.media;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hdh.band_project.post.Post;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AwsS3Service {

    private final MediaRepository mediaRepository;

    private final Cloudinary cloudinary;

    public void uploadMedia(Post post, List<MultipartFile> files){
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String contentType = file.getContentType();
                MediaType mediaType = null;
                Long fileSize = file.getSize();

                if (contentType != null) {
                    if (contentType.startsWith("image/")) {
                        mediaType = MediaType.IMAGE;
                    } else if (contentType.startsWith(("video/"))) {
                        mediaType = MediaType.VIDEO;
                    }
                }

                String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();

                    String mediaUrl = uploadToCloudinary(file, fileName);
                    if(mediaUrl!=null && !mediaUrl.isBlank()) {
                        Media media = new Media(post, mediaType, mediaUrl, fileName, fileSize);
                        mediaRepository.save(media);
                    }
            }
        }
    }

    public String uploadProfileImg(MultipartFile file){
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
            String mediaUrl = uploadToCloudinary(file, fileName);
            return mediaUrl;
    }

    private String uploadToCloudinary(MultipartFile file, String fileName){
        try{
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "public_id", fileName
            ));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    private String extractPublicId(String fileUrl) {
        if (fileUrl == null) return null;

        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex == -1) return null;

        return fileUrl.substring(lastSlashIndex + 1);
    }

    public void deleteFile(String fileName){
        if(fileName == null || fileName.isBlank()) return;

        try {
                cloudinary.uploader().destroy(fileName, ObjectUtils.emptyMap());
            } catch (Exception e) {
                throw new RuntimeException("파일 삭제 실패", e);
            }
    }

    public void deleteProfileImg(String profileUrl){
        String publicId = extractPublicId(profileUrl);
        deleteFile(publicId);
    }
}
