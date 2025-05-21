package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    /**
     * id = уникальный идентификатор пользователя.
     */
    private Long id;
    /**
     * email = электронная почта пользователя.
     */
    private String email;
    /**
     * login = логин пользователя.
     */
    private String login;

    /**
     * name = имя пользователя.
     */
    private String name;
    /**
     * birthday = дата рождения пользователя.
     */
    private LocalDate birthday;

}
