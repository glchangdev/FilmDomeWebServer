package com.filmdome.webserver.controller;

import com.filmdome.movies.repository.MoviesRepository;
import com.filmdome.webserver.util.MovieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.filmdome.webserver.dto.MovieSearchDto;
import java.util.*;

@RestController
@RequestMapping("/api/movies")
public class MovieRestController {

    private MoviesRepository moviesRepository;

    /**
     * Constructor injection for the movie repository.
     *
     * @param moviesRepository Repository used to access movie data
     */
    @Autowired
    public MovieRestController(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    /**
     * Searches the movie database for movies matching
     * the supplied search text.
     *
     * Results are converted into lightweight DTOs before
     * being returned as JSON to the client.
     *
     * @param title Movie title or search text
     * @return List of matching movies
     */
    @GetMapping("/searchMovie")
    public List<MovieSearchDto> searchMovie(@RequestParam String title) {

        // Search the database and convert the results into DTOs.
        return MovieUtil.convertTo(moviesRepository.searchEverything(title));
    }
}