package com.douban.mapper;

import com.douban.entity.Review;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ReviewMapper {

    @Select("SELECT r.*, u.username, u.avatar, rt.score as user_rating FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN ratings rt ON r.user_id = rt.user_id AND r.movie_id = rt.movie_id " +
            "WHERE r.movie_id = #{movieId} ORDER BY r.created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<Review> findByMovie(@Param("movieId") Long movieId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM reviews WHERE movie_id = #{movieId}")
    int countByMovie(@Param("movieId") Long movieId);

    @Select("SELECT r.*, u.username, u.avatar, rt.score as user_rating FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN ratings rt ON r.user_id = rt.user_id AND r.movie_id = rt.movie_id " +
            "WHERE r.id = #{id}")
    Review findById(Long id);

    @Insert("INSERT INTO reviews (user_id, movie_id, title, content) VALUES (#{userId}, #{movieId}, #{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Review review);

    @Update("UPDATE reviews SET title = #{title}, content = #{content} WHERE id = #{id} AND user_id = #{userId}")
    int update(Review review);

    @Delete("DELETE FROM reviews WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);

    @Update("UPDATE reviews SET likes = likes + 1 WHERE id = #{id}")
    int incrementLikes(@Param("id") Long id);

    @Delete("DELETE FROM reviews WHERE id = #{id}")
    int adminDelete(@Param("id") Long id);

    @Select("SELECT r.*, u.username, u.avatar, rt.score as user_rating, " +
            "m.title as movie_title, m.poster as movie_poster, m.year as movie_year " +
            "FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN ratings rt ON r.user_id = rt.user_id AND r.movie_id = rt.movie_id " +
            "LEFT JOIN movies m ON r.movie_id = m.id " +
            "ORDER BY r.likes DESC, r.created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Review> findTopLiked(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM reviews")
    int countAll();
}
