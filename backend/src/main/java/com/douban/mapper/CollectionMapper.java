package com.douban.mapper;

import com.douban.entity.MovieCollection;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CollectionMapper {

        @Select("SELECT c.*, m.id as movie_id, m.title, m.original_title, m.year, m.directors, " +
                        "m.genres, m.rating, m.rating_count, m.poster, m.duration " +
                        "FROM collections c LEFT JOIN movies m ON c.movie_id = m.id " +
                        "WHERE c.user_id = #{userId} AND c.status = #{status} " +
                        "ORDER BY c.created_at DESC LIMIT #{limit} OFFSET #{offset}")
        @Results({
                        @Result(property = "id", column = "id"),
                        @Result(property = "userId", column = "user_id"),
                        @Result(property = "movieId", column = "movie_id"),
                        @Result(property = "status", column = "status"),
                        @Result(property = "createdAt", column = "created_at"),
                        @Result(property = "movie.id", column = "movie_id"),
                        @Result(property = "movie.title", column = "title"),
                        @Result(property = "movie.originalTitle", column = "original_title"),
                        @Result(property = "movie.year", column = "year"),
                        @Result(property = "movie.directors", column = "directors"),
                        @Result(property = "movie.genres", column = "genres"),
                        @Result(property = "movie.rating", column = "rating"),
                        @Result(property = "movie.ratingCount", column = "rating_count"),
                        @Result(property = "movie.poster", column = "poster"),
                        @Result(property = "movie.duration", column = "duration")
        })
        List<MovieCollection> findByUserAndStatus(@Param("userId") Long userId, @Param("status") String status,
                        @Param("limit") int limit, @Param("offset") int offset);

        @Select("SELECT COUNT(*) FROM collections WHERE user_id = #{userId} AND status = #{status}")
        int countByUserAndStatus(@Param("userId") Long userId, @Param("status") String status);

        @Select("SELECT * FROM collections WHERE user_id = #{userId} AND movie_id = #{movieId}")
        MovieCollection findByUserAndMovie(@Param("userId") Long userId, @Param("movieId") Long movieId);

        @Insert("INSERT INTO collections (user_id, movie_id, status) VALUES (#{userId}, #{movieId}, #{status})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(MovieCollection collection);

        @Update("UPDATE collections SET status = #{status} WHERE user_id = #{userId} AND movie_id = #{movieId}")
        int updateStatus(@Param("userId") Long userId, @Param("movieId") Long movieId, @Param("status") String status);

        @Delete("DELETE FROM collections WHERE user_id = #{userId} AND movie_id = #{movieId}")
        int delete(@Param("userId") Long userId, @Param("movieId") Long movieId);

        @Select("SELECT status FROM collections WHERE user_id = #{userId} AND movie_id = #{movieId}")
        String getStatus(@Param("userId") Long userId, @Param("movieId") Long movieId);
}
