package com.filmdome.webserver.controller;

import com.filmdome.movies.repository.MoviesRepository;
import com.filmdome.webserver.repository.NewsRepository;
import com.filmdome.webserver.util.MovieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Controller
public class NavigationController {

    private final NewsRepository newsRepository;

    private final MoviesRepository moviesRepository;

    Date end = new Date();

    /**
     * Date representing 30 days before today.
     * Used as the lower limit when retrieving
     * newly released movies.
     */
    Date start = Date.from(Instant.now().minus(30, ChronoUnit.DAYS));

    /**
     * Constructor injection for required repositories.
     *
     * @param newsRepository Repository used to retrieve news articles
     * @param moviesRepository Repository used to retrieve movie data
     */
    @Autowired
    public NavigationController(NewsRepository newsRepository, MoviesRepository moviesRepository) {
        this.newsRepository = newsRepository;
        this.moviesRepository = moviesRepository;
    }

    /**
     * Displays the application's home page.
     *
     * The page includes a list of trending movies
     * and movies released within the last 30 days.
     *
     * @param theModel Model used to pass data to the view
     * @return Home page
     */
    @GetMapping("/displayHomePage")
    public String displayHomePage(Model theModel) {

        // Retrieve highly popular movies.
        theModel.addAttribute("trendingMovies", moviesRepository.findByPopularityGreaterThanOrderByPopularityDesc(50.0));

        // Retrieve movies released during the last 30 days.
        theModel.addAttribute("newestMovies", moviesRepository.findByReleaseDateBetweenOrderByReleaseDateDesc(start, end));

        return "home-page";
    }

    /**
     * Displays the search page.
     *
     * Trending and newly released movies are included
     * so that the page has content before a user performs
     * a search.
     *
     * @param theModel Model used to pass data to the view
     * @return Search page
     */
    @GetMapping("/displaySearchPage")
    public String displaySearchPage(Model theModel) {

        // Populate trending movies.
        theModel.addAttribute("trendingMovies", moviesRepository.findByPopularityGreaterThanOrderByPopularityDesc(50.0));

        // Populate newly released movies.
        theModel.addAttribute("newestMovies", moviesRepository.findByReleaseDateBetweenOrderByReleaseDateDesc(start, end));

        return "searched-page";
    }

    /**
     * Displays the news page containing every
     * news article stored in the database.
     *
     * @param theModel Model used to pass data to the view
     * @return News page
     */
    @GetMapping("/newsPage")
    public String newsPage(Model theModel) {

        // Retrieve all news articles.
        theModel.addAttribute("allNews", newsRepository.findAll());

        return "news";
    }
}