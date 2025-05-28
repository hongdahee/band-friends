package com.hdh.band_project.chat;

import com.hdh.band_project.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;

    private String nickname;

    private String senderUniqueId;

    private String message;

    private LocalDateTime createdAt;
}
