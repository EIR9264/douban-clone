package com.douban.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ReviewLikeMapper {

    @Select("SELECT COUNT(*) FROM review_likes WHERE user_id = #{userId} AND review_id = #{reviewId}")
    int exists(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    @Insert("INSERT INTO review_likes (user_id, review_id) VALUES (#{userId}, #{reviewId})")
    int insert(@Param("userId") Long userId, @Param("reviewId") Long reviewId);

    @Delete("DELETE FROM review_likes WHERE user_id = #{userId} AND review_id = #{reviewId}")
    int delete(@Param("userId") Long userId, @Param("reviewId") Long reviewId);
}

