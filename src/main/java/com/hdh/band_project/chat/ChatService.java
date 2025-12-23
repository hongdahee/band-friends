package com.hdh.band_project.chat;

import com.hdh.band_project.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;

    public void save(Long roomId, String senderUniqueId, String nickname, String message){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(roomId);
        chatMessage.setSenderUniqueId(senderUniqueId);
        chatMessage.setNickname(nickname);

        chatMessage.setMessage(processUrl(message));
        chatMessage.setCreatedAt(LocalDateTime.now());

        messageRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessageList(Long chatRoomId){
        return messageRepository.findByRoomId(chatRoomId);
    }

    public void deleteByRoomId(Long chatRoomId){
        messageRepository.deleteByRoomId(chatRoomId);
    }

    public static String processUrl(String message) {
        if (message != null) {
            if (message.startsWith("http://") || message.startsWith("https://")) {
                return "<a href=\"" + message + "\" target=\"_blank\">" + message + "</a>";
            }
            else if (message.startsWith("www.")) {
                return "<a href=\"https://" + message + "\" target=\"_blank\">" + message + "</a>";
            }
        }
        return message;
    }

}
