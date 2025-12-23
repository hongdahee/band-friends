package com.hdh.band_project.chat;

import com.hdh.band_project.band.Band;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    public ChatRoom create(Band band){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setBand(band);
        return chatRoomRepository.save(chatRoom);
    }

    public void delete(ChatRoom chatRoom){
        chatRoomRepository.delete(chatRoom);
        chatService.deleteByRoomId(chatRoom.getId());
    }
}
