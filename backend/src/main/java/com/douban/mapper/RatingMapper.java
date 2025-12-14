package com.douban.mapper;

import com.douban.entity.Rating;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RatingMapper {

    @Select("SELECT * FROM ratings WHERE user_id = #{userId} AND movie_id = #{movieId}")
    Rating findByUserAndMovie(@Param("userId") Long userId, @Param("movieId") Long movieId);

    @Select("SELECT AVG(score) FROM ratings WHERE movie_id = #{movieId}")
    Double getAverageScore(@Param("movieId") Long movieId);

    @Select("SELECT COUNT(*) FROM ratings WHERE movie_id = #{movieId}")
    int countByMovie(@Param("movieId") Long movieId);

    @Insert("INSERT INTO ratings (user_id, movie_id, score) VALUES (#{userId}, #{movieId}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Rating rating);

    @Update("UPDATE ratings SET score = #{score} WHERE user_id = #{userId} AND movie_id = #{movieId}")
    int update(Rating rating);

    @Delete("DELETE FROM ratings WHERE user_id = #{userId} AND movie_id = #{movieId}")
    int delete(@Param("userId") Long userId, @Param("movieId") Long movieId);

    @Select("SELECT r.*, u.username, u.avatar FROM ratings r LEFT JOIN users u ON r.user_id = u.id WHERE r.movie_id = #{movieId} ORDER BY r.created_at DESC")
    List<Rating> findByMovie(@Param("movieId") Long movieId);
}
