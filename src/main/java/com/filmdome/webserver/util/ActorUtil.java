package com.filmdome.webserver.util;

import com.filmdome.movies.entity.Actor;
import com.filmdome.movies.entity.ActorMovie;
import com.filmdome.webserver.dto.ActorDto;
import com.filmdome.webserver.dto.MovieDto;
import java.util.ArrayList;
import java.util.List;

public class ActorUtil {

    public static List<ActorDto> convertTo(List<ActorMovie> actorMovieList) {
        List<ActorDto> actorDtoList = new ArrayList<>();

        for (ActorMovie am : actorMovieList) {
            actorDtoList.add(convertTo(am.getActor()));
        }

        return actorDtoList;
    }

    public static ActorDto convertTo(Actor a) {
        ActorDto actorDto = new ActorDto();

        actorDto.setId(a.getId());
        actorDto.setActorName(a.getActorName());
        actorDto.setPoster(a.getProfilePath());
        actorDto.setSummary(a.getSummary());

        List<MovieDto> movies = new ArrayList<>();

        for (ActorMovie actorMovie : a.getMovies()) {
            movies.add(MovieUtil.convertToMovie(actorMovie.getMovie()));
        }

        actorDto.setMovies(movies);

        return actorDto;
    }
}