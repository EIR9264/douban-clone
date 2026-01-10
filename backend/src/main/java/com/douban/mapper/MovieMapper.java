package com.douban.mapper;

import com.douban.entity.Movie;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MovieMapper {

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "WHERE m.id = #{id}")
    Movie findById(Long id);

    @Insert("INSERT INTO movies (title, original_title, year, directors, actors, genres, country, language, duration, rating, rating_count, summary, poster, images) " +
            "VALUES (#{title}, #{originalTitle}, #{year}, #{directors}, #{actors}, #{genres}, #{country}, #{language}, #{duration}, #{rating}, #{ratingCount}, #{summary}, #{poster}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Movie movie);

    @Update("UPDATE movies SET title = #{title}, original_title = #{originalTitle}, year = #{year}, directors = #{directors}, actors = #{actors}, genres = #{genres}, country = #{country}, language = #{language}, duration = #{duration}, summary = #{summary}, poster = #{poster}, images = #{images} WHERE id = #{id}")
    int update(Movie movie);

    @Delete("DELETE FROM movies WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "ORDER BY rating DESC, rating_count DESC LIMIT #{limit} OFFSET #{offset}")
    List<Movie> findAll(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM movies")
    int count();

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "ORDER BY rating DESC, rating_count DESC LIMIT #{limit}")
    List<Movie> findTopRated(@Param("limit") int limit);

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "ORDER BY m.created_at DESC LIMIT #{limit}")
    List<Movie> findRecent(@Param("limit") int limit);

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "WHERE m.title LIKE CONCAT('%', #{keyword}, '%') OR m.original_title LIKE CONCAT('%', #{keyword}, '%') OR m.directors LIKE CONCAT('%', #{keyword}, '%') OR m.actors LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY rating DESC, rating_count DESC LIMIT #{limit} OFFSET #{offset}")
    List<Movie> search(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM movies WHERE title LIKE CONCAT('%', #{keyword}, '%') OR original_title LIKE CONCAT('%', #{keyword}, '%') OR directors LIKE CONCAT('%', #{keyword}, '%') OR actors LIKE CONCAT('%', #{keyword}, '%')")
    int countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "WHERE m.genres LIKE CONCAT('%', #{genre}, '%') " +
            "ORDER BY rating DESC, rating_count DESC LIMIT #{limit} OFFSET #{offset}")
    List<Movie> findByGenre(@Param("genre") String genre, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM movies WHERE genres LIKE CONCAT('%', #{genre}, '%')")
    int countByGenre(@Param("genre") String genre);

    @Select("""
            <script>
            SELECT
              m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration,
              COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count,
              m.summary, m.poster, m.images, m.created_at, m.updated_at
            FROM movies m
            LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt
            ON m.id = rt.movie_id
            WHERE 1=1
            <foreach collection="genres" item="g">
              AND m.genres LIKE CONCAT('%', #{g}, '%')
            </foreach>
            ORDER BY rating DESC, rating_count DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<Movie> findByGenresAll(@Param("genres") List<String> genres, @Param("limit") int limit, @Param("offset") int offset);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM movies m
            WHERE 1=1
            <foreach collection="genres" item="g">
              AND m.genres LIKE CONCAT('%', #{g}, '%')
            </foreach>
            </script>
            """)
    int countByGenresAll(@Param("genres") List<String> genres);

    @Update("UPDATE movies SET rating = #{rating}, rating_count = #{ratingCount} WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("rating") java.math.BigDecimal rating, @Param("ratingCount") int ratingCount);

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at, " +
            "COALESCE(r.review_count, 0) AS review_count " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "LEFT JOIN (SELECT movie_id, COUNT(*) AS review_count FROM reviews GROUP BY movie_id) r " +
            "ON m.id = r.movie_id " +
            "ORDER BY COALESCE(review_count, 0) DESC, rating DESC, rating_count DESC " +
            "LIMIT #{limit}")
    List<Movie> findMostReviewed(@Param("limit") int limit);

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at, " +
            "COALESCE(c.wish_count, 0) AS wish_count " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "LEFT JOIN (SELECT movie_id, COUNT(*) AS wish_count FROM collections WHERE status = 'wish' GROUP BY movie_id) c " +
            "ON m.id = c.movie_id " +
            "ORDER BY wish_count DESC, rating_count DESC, rating DESC " +
            "LIMIT #{limit}")
    List<Movie> findMostWished(@Param("limit") int limit);

    @Select("SELECT " +
            "m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration, " +
            "COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count, " +
            "m.summary, m.poster, m.images, m.created_at, m.updated_at, " +
            "COALESCE(c.watched_count, 0) AS watched_count " +
            "FROM movies m " +
            "LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt " +
            "ON m.id = rt.movie_id " +
            "LEFT JOIN (SELECT movie_id, COUNT(*) AS watched_count FROM collections WHERE status = 'watched' GROUP BY movie_id) c " +
            "ON m.id = c.movie_id " +
            "ORDER BY watched_count DESC, rating_count DESC, rating DESC " +
            "LIMIT #{limit}")
    List<Movie> findMostWatched(@Param("limit") int limit);

    @Select("""
            <script>
            SELECT
              m.id, m.title, m.original_title, m.year, m.directors, m.actors, m.genres, m.country, m.language, m.duration,
              COALESCE(rt.rating, 0) AS rating, COALESCE(rt.rating_count, 0) AS rating_count,
              m.summary, m.poster, m.images, m.created_at, m.updated_at
            FROM movies m
            LEFT JOIN (SELECT movie_id, ROUND(AVG(score), 1) AS rating, COUNT(*) AS rating_count FROM ratings GROUP BY movie_id) rt
            ON m.id = rt.movie_id
            WHERE m.id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">
              #{id}
            </foreach>
            ORDER BY FIELD(m.id,
            <foreach collection="ids" item="id" separator=",">
              #{id}
            </foreach>
            )
            </script>
            """)
    List<Movie> findByIdsOrdered(@Param("ids") List<Long> ids);
}
