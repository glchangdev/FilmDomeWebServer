package com.filmdome.webserver.util;

import com.filmdome.movies.entity.Movie;
import com.filmdome.webserver.dto.MovieDto;
import com.filmdome.webserver.dto.MovieSearchDto;
import java.util.ArrayList;
import java.util.List;

public class MovieUtil {

    public static List<MovieSearchDto> convertTo(List<Movie> movies) {
        List<MovieSearchDto> movieDtoList = new ArrayList<>();

        for (Movie m : movies) {
            movieDtoList.add(convertTo(m));
        }

        return movieDtoList;
    }

    public static MovieSearchDto convertTo(Movie m) {
        MovieSearchDto movieDto = new MovieSearchDto();

        movieDto.setId(m.getId());
        movieDto.setPoster(m.getPoster());
        movieDto.setName(m.getName());

        return movieDto;
    }

    public static List<MovieDto> convertMovieList(List<Movie> movies) {
        List<MovieDto> movieBioDtoList = new ArrayList<>();

        for (Movie mb : movies) {
            movieBioDtoList.add(convertMovie(mb));
        }

        return movieBioDtoList;
    }

    public static MovieDto convertMovie(Movie mb) {
        MovieDto movieBioDto = new MovieDto();

        movieBioDto.setId(mb.getId());
        movieBioDto.setName(mb.getName());
        movieBioDto.setRunTime(mb.getRunTime());
        movieBioDto.setRating(mb.getRating());
        movieBioDto.setReleaseDate(mb.getReleaseDate());
        movieBioDto.setPoster(mb.getPoster());
        movieBioDto.setTrailer(mb.getTrailer());
        movieBioDto.setDescription(mb.getDescription());
        movieBioDto.setDirector(mb.getDirector());
        movieBioDto.setStudios(StudioUtil.convertTo(mb.getStudios()));
        movieBioDto.setActors(ActorUtil.convertTo(mb.getActors()));

        return movieBioDto;
    }

    public static MovieDto convertToMovie(Movie movie) {

        MovieDto movieDto = new MovieDto();
        movieDto.setId(movie.getId());
        movieDto.setName(movie.getName());
        movieDto.setPoster(movie.getPoster());

        return movieDto;
    }
}