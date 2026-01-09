package com.douban.service;

import com.douban.dto.PageResult;
import com.douban.dto.MovieRequest;
import com.douban.entity.Movie;
import com.douban.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieMapper movieMapper;

    public PageResult<Movie> getMovies(int page, int size) {
        int offset = (page - 1) * size;
        List<Movie> movies = movieMapper.findAll(size, offset);
        int total = movieMapper.count();
        return new PageResult<>(movies, page, size, total);
    }

    public Movie getMovieById(Long id) {
        Movie movie = movieMapper.findById(id);
        if (movie == null) {
            throw new RuntimeException("电影不存在");
        }
        return movie;
    }

    public List<Movie> getTopRated(int limit) {
        return movieMapper.findTopRated(limit);
    }

    public List<Movie> getRecent(int limit) {
        return movieMapper.findRecent(limit);
    }

    public List<Movie> getMostReviewed(int limit) {
        return movieMapper.findMostReviewed(limit);
    }

    public List<Movie> getMostWished(int limit) {
        return movieMapper.findMostWished(limit);
    }

    public List<Movie> getMostWatched(int limit) {
        return movieMapper.findMostWatched(limit);
    }

    public PageResult<Movie> search(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Movie> movies = movieMapper.search(keyword, size, offset);
        int total = movieMapper.countByKeyword(keyword);
        return new PageResult<>(movies, page, size, total);
    }

    public PageResult<Movie> getByGenre(String genre, int page, int size) {
        int offset = (page - 1) * size;
        List<Movie> movies = movieMapper.findByGenre(genre, size, offset);
        return new PageResult<>(movies, page, size, movies.size());
    }

    public Movie create(MovieRequest request) {
        Movie movie = new Movie();
        applyRequest(movie, request);
        movie.setRating(java.math.BigDecimal.ZERO);
        movie.setRatingCount(0);
        movieMapper.insert(movie);
        return movie;
    }

    public Movie update(Long id, MovieRequest request) {
        Movie existing = movieMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("电影不存在");
        }
        applyRequest(existing, request);
        movieMapper.update(existing);
        return movieMapper.findById(id);
    }

    public void delete(Long id) {
        movieMapper.delete(id);
    }

    private void applyRequest(Movie movie, MovieRequest request) {
        movie.setTitle(request.getTitle());
        movie.setOriginalTitle(request.getOriginalTitle());
        movie.setYear(request.getYear());
        movie.setDirectors(request.getDirectors());
        movie.setActors(request.getActors());
        movie.setGenres(request.getGenres());
        movie.setCountry(request.getCountry());
        movie.setLanguage(request.getLanguage());
        movie.setDuration(request.getDuration());
        movie.setSummary(request.getSummary());
        movie.setPoster(request.getPoster());
        movie.setImages(request.getImages());
    }
}
