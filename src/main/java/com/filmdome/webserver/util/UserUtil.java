package com.filmdome.webserver.util;

import com.filmdome.webserver.dto.UserDisplayDto;
import com.filmdome.webserver.dto.UserDto;
import com.filmdome.webserver.entity.User;
import java.util.ArrayList;
import java.util.List;

public class UserUtil {

    public static List<UserDto> convertTo(List<User> users) {

        List<UserDto> dtoList = new ArrayList<>();

        for (User user : users) {
            dtoList.add(convertTo(user));
        }

        return dtoList;
    }

    public static UserDto convertTo(User user) {

        UserDto dto = null;

        if (user != null) {
            dto = new UserDto();

            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setPassword(user.getPassword());
            dto.setPhoneNumber(user.getPhoneNumber());
        }

        return dto;
    }

    public static UserDisplayDto convertToDisplayDto(User user) {

        UserDisplayDto dto = new UserDisplayDto();

        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());

        return dto;
    }

    public static UserDisplayDto getGuestUserDisplayDto() {

        UserDisplayDto dto = new UserDisplayDto();

        dto.setId(-1);
        dto.setFirstName("Guest");
        dto.setLastName("Guest");

        return dto;
    }

    public static User convertTo(UserDto dto) {

        User user = new User();

        if (dto.getId() != null) {
            user.setId(dto.getId());
        }
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());

        return user;
    }
}