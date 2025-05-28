package com.hdh.band_project.band;

import com.hdh.band_project.DataNotFoundException;
import com.hdh.band_project.chat.ChatRoom;
import com.hdh.band_project.chat.ChatRoomService;
import com.hdh.band_project.media.AwsS3Service;
import com.hdh.band_project.media.Media;
import com.hdh.band_project.post.Post;
import com.hdh.band_project.post.PostService;
import com.hdh.band_project.recruitment.RecruitmentService;
import com.hdh.band_project.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BandService {

    private final BandRepository bandRepository;
    private final BandMemberService bandMemberService;
    private final ChatRoomService chatRoomService;
    private final PostService postService;
    private final RecruitmentService recruitmentService;
    private final AwsS3Service awsS3Service;

    public Band getBand(Long id) {
        Optional<Band> band = bandRepository.findById(id);
        if (band.isPresent()) {
            return band.get();
        } else {
            throw new DataNotFoundException("band not found");
        }
    }

    public Band getBandByChatRoomId(Long chatRoomId) {
        Optional<Band> band = bandRepository.findByChatRoomId(chatRoomId);
        if (band.isPresent()) {
            return band.get();
        } else {
            throw new DataNotFoundException("band not found");
        }
    }

    public List<Band> getBandWithMemberRole(SiteUser user) {
        return user.getBandList().stream()
                .filter(bm -> bm.getMemberRole() == MemberRole.LEADER || bm.getMemberRole() == MemberRole.STAFF)
                .map(BandMember::getBand)
                .collect(Collectors.toList());
    }

    public Long create(String bandName, Genre genre, String question,
                       String regionName, Double latitude, Double longitude){
        Band band = new Band();
        band.setBandName(bandName);
        band.setRegionName(regionName);
        band.setLatitude(latitude);
        band.setLongitude(longitude);
        band.setGenre(genre);
        band.setQuestion(question);
        return bandRepository.save(band).getId();
    }

    public void registerChatRoom(Band band, ChatRoom chatRoom){
        band.setChatRoom(chatRoom);
        bandRepository.save(band);
    }

    public void modify(Long bandId, BandForm bandForm, SiteUser user){
        BandMember bandMember = bandMemberService.getBandMember(user, bandId);
        boolean isLeader = bandMemberService.checkLeader(bandMember);
        if(isLeader) {
            Band band = getBand(bandId);
            band.setBandName(bandForm.getBandName());
            band.setQuestion(bandForm.getQuestion());
            band.setGenre(bandForm.getGenre());
            bandRepository.save(band);
        }
    }

    public void updateProfile(Long bandId, MultipartFile profile, SiteUser user){
        BandMember bandMember = bandMemberService.getBandMember(user, bandId);
        boolean isLeader = bandMemberService.checkLeader(bandMember);

        if(isLeader) {
            Band band = getBand(bandId);
            String originalProfileUrl = null;
            if (band.getProfileImg() != null) {
                originalProfileUrl = band.getProfileImg();
            }
            String profileUrl = awsS3Service.uploadProfileImg(profile);
            if(profileUrl!=null && !profileUrl.isBlank()) {
                band.setProfileImg(profileUrl);
                bandRepository.save(band);
                if (originalProfileUrl != null) {
                    awsS3Service.deleteFile(originalProfileUrl);
                }
            }
        }
    }

    public void delete(Long bandId, SiteUser user) {
        Band band = getBand(bandId);
        BandMember bandMember = bandMemberService.getBandMember(user, bandId);
        if(bandMemberService.checkLeader(bandMember)){
            deleteBandInfo(band);
        }
    }

    public void quit(Long bandId, SiteUser user) {
        Band band = getBand(bandId);
        BandMember bandMember = bandMemberService.getBandMember(user, bandId);
        boolean isLeader = bandMemberService.isLeader(bandMember);
        bandMemberService.quitBand(bandMember.getId());
        postService.deleteByBandAndAuthor(band, user);
        if(isLeader){
            if(bandMemberService.getMemberCountExceptLeader(bandId)==0) {
                deleteBandInfo(band);
            }
            else{
                bandMemberService.getStaff(band)
                        .ifPresentOrElse(
                                staff -> bandMemberService.changeMemberRole(staff, MemberRole.LEADER),
                                () -> {
                                    BandMember firstMember = bandMemberService.getFirstMember(band);
                                    bandMemberService.changeMemberRole(firstMember, MemberRole.LEADER);
                                }
                        );
            }
        }
    }

    public List<BandMember> sortMemberList(Band band){
        List<BandMember> sortedMemberList = band.getMemberList().stream()
                .sorted(Comparator
                        .comparing((BandMember m) -> m.getMemberRole() != MemberRole.LEADER)
                        .thenComparing(BandMember::getCreatedAt)
                )
                .toList();
        return sortedMemberList;
    }

    private void deleteBandInfo(Band band){
        recruitmentService.deleteByAppliedBand(band);
        chatRoomService.delete(band.getChatRoom());
        postService.deleteByBand(band);
        bandRepository.delete(band);
        if(band.getProfileImg()!=null && !band.getProfileImg().isBlank()) {
            awsS3Service.deleteFile(band.getProfileImg());
            deleteBandPostsImg(band);
        }
    }

    private void deleteBandPostsImg(Band band){
        List<Post> postList = postService.getPostsByBand(band);
        for(Post post : postList){
            for(Media media : post.getMediaList()){
                awsS3Service.deleteFile(media.getFilePath());
            }
        }
    }
}
