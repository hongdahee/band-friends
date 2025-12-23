package com.hdh.band_project.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/{chatRoomId}/message")
    @SendTo("/sub/{chatRoomId}")
    public ChatMessage chat(@DestinationVariable("chatRoomId") Long chatRoomId,
                     ChatMessage chatMessage){
        chatService.save(chatRoomId, chatMessage.getSenderUniqueId(),
                chatMessage.getNickname(), chatMessage.getMessage());
        return chatMessage;
    }
}
