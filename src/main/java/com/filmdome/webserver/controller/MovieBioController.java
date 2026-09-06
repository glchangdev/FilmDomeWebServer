package com.filmdome.webserver.controller;

import com.filmdome.movies.repository.ActorRepository;
import com.filmdome.movies.repository.MoviesRepository;
import com.filmdome.movies.entity.*;
import com.filmdome.webserver.util.ActorUtil;
import com.filmdome.webserver.util.MovieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MovieBioController {

    private final MoviesRepository moviesRepository;

    private final ActorRepository actorRepository;

    /**
     * Constructor injection for the required repositories.
     *
     * @param moviesRepository Repository used to retrieve movie data
     * @param actorRepository Repository used to retrieve actor data
     */
    @Autowired
    public MovieBioController(MoviesRepository moviesRepository, ActorRepository actorRepository) {
        this.moviesRepository = moviesRepository;
        this.actorRepository = actorRepository;
    }

    /**
     * Displays detailed information for a selected movie.
     *
     * The movie is retrieved using its unique database ID,
     * converted into a DTO, and sent to the view.
     *
     * @param id Movie ID
     * @param model Model used to pass data to the view
     * @return Movie information page
     */
    @RequestMapping(value = "/displayMovieInfo", method = RequestMethod.GET)
    public String displayMovieInfo(@RequestParam("id") int id, Model model) {

        // Retrieve the selected movie and convert it into a display DTO.
        model.addAttribute("movie", MovieUtil.convertMovie(moviesRepository.findById(id)));

        return "movie-bio";
    }

    /**
     * Displays detailed information for a selected actor.
     *
     * The actor is retrieved using its unique database ID,
     * converted into a DTO, and sent to the view.
     *
     * @param id Actor ID
     * @param model Model used to pass data to the view
     * @return Actor information page
     */
    @RequestMapping(value = "/displayActorInfo", method = RequestMethod.GET)
    public String displayActorInfo(@RequestParam("id") int id, Model model) {

        // Retrieve the selected actor.
        // Convert the actor into a display DTO for the view.
        Actor actor = actorRepository.findById(id);
        model.addAttribute("actor", ActorUtil.convertTo(actor));

        return "actor-bio";
    }
}