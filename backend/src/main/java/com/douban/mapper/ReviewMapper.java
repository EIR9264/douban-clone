package com.douban.mapper;

import com.douban.entity.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Select("""
            SELECT
              r.id, r.user_id, r.movie_id, r.title, r.content, r.created_at, r.updated_at,
              u.username, u.avatar,
              rt.score AS user_rating,
              COALESCE(lc.like_count, 0) AS like_count,
              (rl.review_id IS NOT NULL) AS liked
            FROM reviews r
            LEFT JOIN users u ON r.user_id = u.id
            LEFT JOIN ratings rt ON r.user_id = rt.user_id AND r.movie_id = rt.movie_id
            LEFT JOIN (SELECT review_id, COUNT(*) AS like_count FROM review_likes GROUP BY review_id) lc ON lc.review_id = r.id
            LEFT JOIN review_likes rl ON rl.review_id = r.id AND rl.user_id = #{viewerId}
            WHERE r.movie_id = #{movieId}
            ORDER BY r.created_at DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<Review> findByMovie(
            @Param("movieId") Long movieId,
            @Param("viewerId") Long viewerId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Select("SELECT COUNT(*) FROM reviews WHERE movie_id = #{movieId}")
    int countByMovie(@Param("movieId") Long movieId);

    @Select("""
            SELECT
              r.id, r.user_id, r.movie_id, r.title, r.content, r.created_at, r.updated_at,
              u.username, u.avatar,
              rt.score AS user_rating,
              COALESCE(lc.like_count, 0) AS like_count,
              (rl.review_id IS NOT NULL) AS liked,
              m.title AS movie_title, m.poster AS movie_poster, m.year AS movie_year
            FROM reviews r
            LEFT JOIN users u ON r.user_id = u.id
            LEFT JOIN ratings rt ON r.user_id = rt.user_id AND r.movie_id = rt.movie_id
            LEFT JOIN movies m ON r.movie_id = m.id
            LEFT JOIN (SELECT review_id, COUNT(*) AS like_count FROM review_likes GROUP BY review_id) lc ON lc.review_id = r.id
            LEFT JOIN review_likes rl ON rl.review_id = r.id AND rl.user_id = #{viewerId}
            WHERE r.id = #{id}
            """)
    Review findById(@Param("id") Long id, @Param("viewerId") Long viewerId);

    @Insert("INSERT INTO reviews (user_id, movie_id, title, content) VALUES (#{userId}, #{movieId}, #{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);

    @Update("UPDATE reviews SET title = #{title}, content = #{content} WHERE id = #{id} AND user_id = #{userId}")
    int update(Review review);

    @Delete("DELETE FROM reviews WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("DELETE FROM reviews WHERE id = #{id}")
    int adminDelete(@Param("id") Long id);

    @Select("""
            SELECT
              r.id, r.user_id, r.movie_id, r.title, r.content, r.created_at, r.updated_at,
              u.username, u.avatar,
              rt.score AS user_rating,
              COALESCE(lc.like_count, 0) AS like_count,
              (rl.review_id IS NOT NULL) AS liked,
              m.title AS movie_title, m.poster AS movie_poster, m.year AS movie_year
            FROM reviews r
            LEFT JOIN users u ON r.user_id = u.id
            LEFT JOIN ratings rt ON r.user_id = rt.user_id AND r.movie_id = rt.movie_id
            LEFT JOIN movies m ON r.movie_id = m.id
            LEFT JOIN (SELECT review_id, COUNT(*) AS like_count FROM review_likes GROUP BY review_id) lc ON lc.review_id = r.id
            LEFT JOIN review_likes rl ON rl.review_id = r.id AND rl.user_id = #{viewerId}
            ORDER BY COALESCE(lc.like_count, 0) DESC, r.created_at DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<Review> findTopLiked(@Param("viewerId") Long viewerId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM reviews")
    int countAll();

    @Select("""
            SELECT
              r.id, r.user_id, r.movie_id, r.title, r.content, r.created_at, r.updated_at,
              u.username, u.avatar,
              rt.score AS user_rating,
              COALESCE(lc.like_count, 0) AS like_count,
              m.title AS movie_title, m.poster AS movie_poster, m.year AS movie_year
            FROM reviews r
            LEFT JOIN users u ON r.user_id = u.id
            LEFT JOIN ratings rt ON r.user_id = rt.user_id AND r.movie_id = rt.movie_id
            LEFT JOIN movies m ON r.movie_id = m.id
            LEFT JOIN (SELECT review_id, COUNT(*) AS like_count FROM review_likes GROUP BY review_id) lc ON lc.review_id = r.id
            ORDER BY r.created_at DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<Review> findAllWithDetails(@Param("limit") int limit, @Param("offset") int offset);
}

