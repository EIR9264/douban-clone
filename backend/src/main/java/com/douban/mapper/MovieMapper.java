package com.douban.mapper;

import com.douban.entity.Movie;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MovieMapper {

    @Select("SELECT * FROM movies WHERE id = #{id}")
    Movie findById(Long id);

    @Select("SELECT * FROM movies ORDER BY rating DESC, rating_count DESC LIMIT #{limit} OFFSET #{offset}")
    List<Movie> findAll(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM movies")
    int count();

    @Select("SELECT * FROM movies ORDER BY rating DESC, rating_count DESC LIMIT #{limit}")
    List<Movie> findTopRated(@Param("limit") int limit);

    @Select("SELECT * FROM movies ORDER BY created_at DESC LIMIT #{limit}")
    List<Movie> findRecent(@Param("limit") int limit);

    @Select("SELECT * FROM movies WHERE title LIKE CONCAT('%', #{keyword}, '%') OR original_title LIKE CONCAT('%', #{keyword}, '%') OR directors LIKE CONCAT('%', #{keyword}, '%') OR actors LIKE CONCAT('%', #{keyword}, '%') ORDER BY rating DESC LIMIT #{limit} OFFSET #{offset}")
    List<Movie> search(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM movies WHERE title LIKE CONCAT('%', #{keyword}, '%') OR original_title LIKE CONCAT('%', #{keyword}, '%') OR directors LIKE CONCAT('%', #{keyword}, '%') OR actors LIKE CONCAT('%', #{keyword}, '%')")
    int countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM movies WHERE genres LIKE CONCAT('%', #{genre}, '%') ORDER BY rating DESC LIMIT #{limit} OFFSET #{offset}")
    List<Movie> findByGenre(@Param("genre") String genre, @Param("limit") int limit, @Param("offset") int offset);

    @Update("UPDATE movies SET rating = #{rating}, rating_count = #{ratingCount} WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("rating") java.math.BigDecimal rating, @Param("ratingCount") int ratingCount);
}
