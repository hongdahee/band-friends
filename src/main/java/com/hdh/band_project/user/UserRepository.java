package com.hdh.band_project.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {

    boolean existsByUniqueId(String uniqueId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<SiteUser> findByUsername(String username);
    Optional<SiteUser> findByEmail(String email);
    Optional<SiteUser> findByUniqueId(String uniqueId);
}
