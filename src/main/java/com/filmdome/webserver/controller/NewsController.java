package com.filmdome.webserver.controller;

import com.filmdome.webserver.repository.NewsRepository;
import com.filmdome.webserver.entity.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NewsController {

    private final NewsRepository newsRepository;

    /**
     * Constructor injection for the news repository.
     *
     * @param newsRepository Repository used to retrieve news articles
     */
    @Autowired
    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * Displays a selected news article.
     *
     * The article is retrieved using its unique database ID.
     * If no article exists with the supplied ID, the user is redirected to an error page.
     *
     * @param id News article ID
     * @param model Model used to pass data to the view
     * @return News information page or error page
     */
    @RequestMapping(value = "/newsSelection", method = RequestMethod.GET)
    public String newsSelection(@RequestParam("id") int id, Model model) {

        // Retrieve the selected news article.
        News news = newsRepository.findById(id);

        // Display the article if it exists.
        if (news != null) {
            model.addAttribute("news", news);
            return "news-info";
        }

        return "error";
    }
}