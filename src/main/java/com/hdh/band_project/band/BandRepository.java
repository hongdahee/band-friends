package com.hdh.band_project.band;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BandRepository extends JpaRepository<Band, Long> {
    Optional<Band> findByChatRoomId(Long chatRoomId);
}
