package com.hdh.band_project.post;

import com.hdh.band_project.band.Band;
import com.hdh.band_project.band.Genre;
import com.hdh.band_project.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>{
    Page<Post> findByCategory(String category, Pageable pageable);
    Page<Post> findByBandIdAndCategory(Long bandId, String category, Pageable pageable);
    List<Post> findByAuthor(SiteUser user);
    List<Post> findByBand(Band band);
    @Query(value = """
    SELECT p.*
    FROM post p
    JOIN band b ON p.band_id = b.id
    WHERE 6371 * acos(
        cos(radians(:userLat)) * cos(radians(b.latitude)) *
        cos(radians(b.longitude) - radians(:userLon)) +
        sin(radians(:userLat)) * sin(radians(b.latitude))
    ) <= 30
""",
            countQuery = """
    SELECT COUNT(p.id)
    FROM post p
    JOIN band b ON p.band_id = b.id
    WHERE 6371 * acos(
        cos(radians(:userLat)) * cos(radians(b.latitude)) *
        cos(radians(b.longitude) - radians(:userLon)) +
        sin(radians(:userLat)) * sin(radians(b.latitude))
    ) <= 30
""",
            nativeQuery = true)
    Page<Post> findPostsWithin30km(@Param("userLat") Double userLat,
                                   @Param("userLon") Double userLon,
                                   Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.category = :category AND (" +
            "   :keyword IS NULL OR " +
            "   LOWER(p.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   LOWER(p.author.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchCommunityPosts(@Param("category") String category,
                      @Param("keyword") String keyword,
                      Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.band.id = :bandId " +
            "AND p.category = :category " +
            "AND (" +
            "   :keyword IS NULL OR " +
            "   LOWER(p.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "   LOWER(p.author.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
    Page<Post> searchBandPosts(
            @Param("bandId") Long bandId,
            @Param("category") String category,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(value = """
    SELECT p.*
    FROM post p
    JOIN band b ON p.band_id = b.id
    LEFT JOIN site_user su ON p.author_id = su.id 
    WHERE p.category = :category
    AND 6371 * acos(
        cos(radians(:userLat)) * cos(radians(b.latitude)) *
        cos(radians(b.longitude) - radians(:userLon)) +
        sin(radians(:userLat)) * sin(radians(b.latitude))
    ) <= 30
    AND (
        :keyword IS NULL OR 
        LOWER(p.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(b.band_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(su.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
        LOWER(b.region_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
            AND (
                     :position IS NULL OR :position = 'all' OR EXISTS (
                         SELECT 1 FROM post_position pp
                         WHERE pp.post_id = p.id AND pp.position_list = :position
                     )
                 )
AND (
    :genre IS NULL OR :genre = 'ALL' OR LOWER(b.genre) = LOWER(:genre)
)
""",
            countQuery = """
    SELECT COUNT(p.id)
    FROM post p
    JOIN band b ON p.band_id = b.id
    LEFT JOIN site_user su ON p.author_id = su.id
    WHERE p.category = :category
    AND 6371 * acos(
        cos(radians(:userLat)) * cos(radians(b.latitude)) *
        cos(radians(b.longitude) - radians(:userLon)) +
        sin(radians(:userLat)) * sin(radians(b.latitude))
    ) <= 30
    AND (
        :keyword IS NULL OR 
        LOWER(p.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(b.band_name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(su.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
        LOWER(b.region_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
AND (
    :position IS NULL OR :position = 'all' OR EXISTS (
        SELECT 1 FROM post_position pp
        WHERE pp.post_id = p.id AND pp.position_list = :position
    )
)
AND (
    :genre IS NULL OR :genre = 'ALL' OR LOWER(b.genre) = LOWER(:genre)
)

""",
            nativeQuery = true)
    Page<Post> searchRecruitPostsWithin30km(
            @Param("category") String category,
            @Param("userLat") Double userLat,
            @Param("userLon") Double userLon,
            @Param("keyword") String keyword,
            @Param("position") String position,
            @Param("genre") Genre genre,
            Pageable pageable
    );
    void deleteByBand(Band band);
    void deleteByBandAndAuthor(Band band, SiteUser author);
}
