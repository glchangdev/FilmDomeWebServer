package com.filmdome.webserver.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class MovieDto {

    private Integer id;

    private String name;

    private Integer runTime;

    private String rating;

    private String poster;

    private String trailer;

    private Date releaseDate;

    private String description;

    private String director;

    private Set<StudioDto> studios;

    private List<ActorDto> actors;

}
