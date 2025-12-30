package com.hdh.band_project.media;

import com.hdh.band_project.DataNotFoundException;
import com.hdh.band_project.post.Post;
import com.hdh.band_project.user.SiteUser;
import com.hdh.band_project.user.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class MediaService {

    private final MediaRepository mediaRepository;
    private final AwsS3Service awsS3Service;

    static final Path UPLOAD_PATH = Paths.get("src/main/resources/static/uploads");

    public Media getMedia(Long id){
        Optional<Media> media = mediaRepository.findById(id);
        if(media.isPresent()){
            return media.get();
        }
        else{
            throw new DataNotFoundException("media not found");
        }
    }

//    public void uploadMedia(Post post, List<MultipartFile> files) {
//       try{
//           for(MultipartFile file : files){
//               String contentType = file.getContentType();
//               MediaType mediaType = null;
//               if (contentType != null) {
//                   if (contentType.startsWith("image/")) {
//                       mediaType = MediaType.IMAGE;
//                   } else if (contentType.startsWith("video/")) {
//                       mediaType = MediaType.VIDEO;
//                   }
//               }
//
//               String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
//               String dbFilePath = saveMedia(file, mediaType, fileName);
//
//               Media media = new Media(post, mediaType, dbFilePath, fileName);
//               mediaRepository.save(media);
//           }
//       } catch (IOException e) {
//           e.printStackTrace();
//       }
//    }

    private String saveMedia(MultipartFile file, MediaType mediaType, String fileName) throws IOException{

        Path filePath = UPLOAD_PATH.resolve(fileName);

        String dbFilePath = "/uploads/" + fileName;

        Files.createDirectories(filePath.getParent());

        if(mediaType.name().equals("IMAGE")){
            Files.write(filePath, file.getBytes());
        }
        else if(mediaType.name().equals("VIDEO")){
            Files.copy(file.getInputStream(), filePath);
        }

        return dbFilePath;
    }

    public void deleteMedia(Long mediaId, String username){
        Media media = getMedia(mediaId);
        if(!media.getPost().getAuthor().getUsername().equals(username)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
//        awsS3Service.deleteFile(media.getFilePath());
        mediaRepository.delete(media);
    }
}
