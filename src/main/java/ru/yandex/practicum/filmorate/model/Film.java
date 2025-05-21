package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@AllArgsConstructor
public class Film {
    /**
     * id = уникальный идентификатор пользователя.
     */
    private long id;
    /**
     * name = название фильма.
     */
    private String name;
    /**
     * description = описание фильма.
     */
    private String description;
    /**
     * releaseDate = дата релиза фильма.
     */
    private LocalDate releaseDate;
    /**
     * duration = продолжительность фильма.
     */
    private int duration;
}
