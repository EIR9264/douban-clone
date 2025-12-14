package com.douban.service;

import com.douban.dto.PageResult;
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
}
