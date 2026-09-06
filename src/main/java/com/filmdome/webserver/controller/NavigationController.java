package com.filmdome.webserver.controller;

import com.filmdome.movies.repository.MoviesRepository;
import com.filmdome.webserver.repository.NewsRepository;
import com.filmdome.webserver.util.UserUtil;
import jakarta.servlet.http.HttpSession;
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
     * This page can be accessed by both logged-in users
     * and guests.
     *
     * @param model Model used to pass data to the view
     * @return Home page
     */
    @GetMapping("/displayHomePage")
    public String displayHomePage(Model model) {

        // Determine the date range for newly released movies.
        Date end = new Date();
        Date start = Date.from(Instant.now().minus(90, ChronoUnit.DAYS));

        // Retrieve highly popular movies.
        model.addAttribute("trendingMovies", moviesRepository.findByPopularityGreaterThanOrderByPopularityDesc(50.0));

        // Retrieve movies released during the last 30 days.
        model.addAttribute("newestMovies", moviesRepository.findByReleaseDateBetweenOrderByReleaseDateDesc(start, end));

        return "home-page";
    }

    @GetMapping("/displayGuestHomePage")
    public String displayGustHomePage(Model theModel, HttpSession session) {

        session.setAttribute("user", UserUtil.getGuestUserDisplayDto());
        return "redirect:/displayHomePage";
    }

    @GetMapping("/displaySearchPage")
    public String displaySearchPage(Model theModel) {
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