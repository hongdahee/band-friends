package com.hdh.band_project.media;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cloudinary.Cloudinary;
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
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;

    private final MediaRepository mediaRepository;
//    private final AmazonS3 amazonS3;
//    private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);


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

//    private String uploadToS3(MultipartFile file, String fileName) throws IOException {
//        try {
//            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null));
//
//            return amazonS3.getUrl(bucket, fileName).toString();
//        } catch (Exception e) {
//            throw new IOException("Failed to upload file to S3", e);
//        }
//    }

    private String extractFileName(String fileUrl){
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if(lastSlashIndex != -1){
            return fileUrl.substring(lastSlashIndex + 1);
        }
        return null;
    }

//    public void deleteFile(String fileUrl){
//        String fileName = extractFileName(fileUrl);
//        if(fileName!=null) {
//            try {
//                amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
//                logger.info("파일 삭제 완료: {}", fileName);
//            } catch (Exception e) {
//                logger.error("파일 삭제 실패: {}", fileName, e);
//            }
//        }
//    }
}
