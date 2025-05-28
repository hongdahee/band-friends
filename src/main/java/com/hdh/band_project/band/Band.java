package com.hdh.band_project.band;

import com.hdh.band_project.chat.ChatRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Band {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bandName;

    private String profileImg;

    @Column(nullable = false)
    private String regionName;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Genre genre;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandMember> memberList;

    @Column(nullable = false)
    private String question;
}
