package com.filmdome.webserver.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ActorDto {

    private Integer id;

    private String actorName;

    private String poster;

    private String summary;

    private List<MovieDto> movies;
}