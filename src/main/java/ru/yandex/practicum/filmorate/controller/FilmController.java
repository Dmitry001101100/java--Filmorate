package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.*;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    private long nextId = 0L;

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public Film getFilm(Long id) {  // поиск по id для тестов
        if (films.containsKey(id)) {
            return films.get(id);
        }
        return null;
    }

    public void clearFilms() {
        films.clear();
        nextId = 0L;
    }


    @PostMapping
    public Film saveFilm(@RequestBody Film film) {
        log.info("new film: {}", film);
        validateFilm(film);
        film.setId(++nextId);
        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {

        if (film.getId() == 0) {
            log.error("Введен id : {}", 0);
            throw new ValidationException("Для изменения данных требуется указать id.");
        } else {
            if (films.containsKey(film.getId())) {
                validateFilm(film);
                films.put(film.getId(), film);
            } else {
                log.error("Нет такого id: {}", film.getId());
                throw new ValidationException("Фильм с данным id не найден.");
            }
        }

        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) { // проверяем на то что название фильма не пустое
            log.error("Пустое название фильма: {}", film.getName());
            throw new ValidationException("Название фильма не может быть пустым.");
        }

        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                log.error("Максимальная длинна описания фильма 200 символов: {}", film.getDescription());
                throw new ValidationException("Описание не может быть больше 200 символов.");
            }
        }

        LocalDate limitDate = LocalDate.of(1985, 12, 28);
        if (film.getReleaseDate().isBefore(limitDate)) {
            log.error("Дата релиза раньше 28.12.1985: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }

        if (film.getDuration() < 0) {
            log.error("Продолжительность отрицательное письмо: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма не может быть отрицательным числом.");
        }
    }
}
