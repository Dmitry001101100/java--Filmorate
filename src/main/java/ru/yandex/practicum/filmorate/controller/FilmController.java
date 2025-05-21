package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 0L;
// ---------------------------------------------------------------------------------------------------------------------

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {  // поиск по id для тестов

        if (films.containsKey(id)) {
            log.info("get film id: {}", films.get(id));
            return films.get(id);
        } else {
            log.error("There is no movie with this id: {}", id);
            throw new NotFoundException(404, "Пользователь с id " + id + " не найден.");
        }
    }

    @DeleteMapping
    public void clearFilms() {
        log.info("create films");
        films.clear();
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
        log.info("Обновление фильма: ", film);

        if (film.getId() <= 0) {
            log.error("Обновление. Введен id : {}", 0);
            throw new NotFoundException(404, "Недопустимое значение id:" + film.getId());
        } else {
            if (films.containsKey(film.getId())) {
                validateFilm(film);
                films.put(film.getId(), film);
            } else {
                log.error("Обновление. Нет такого id: {}", film.getId());
                throw new NotFoundException(404, "Фильм с id " + film.getId() + " не найден");
            }
        }

        return film;
    }

    //-------------------------------------------------------------------------------------------------------------------

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) { // проверяем на то что название фильма не пустое
            log.error("Пустое название фильма: {}", film.getName());
            throw new ValidationException(400, "Название фильма не может быть пустым.");
        }

        if (film.getDescription() != null) {
            if (film.getDescription().length() > 200) {
                log.error("Максимальная длинна описания фильма 200 символов: {}", film.getDescription());
                throw new ValidationException(400, "Описание не может быть больше 200 символов.");
            }
        }

        LocalDate limitDate = LocalDate.of(1985, 12, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(limitDate)) {
            log.error("Дата релиза раньше 28.12.1985: {}", film.getReleaseDate());
            throw new ValidationException(400, "Дата релиза — не раньше 28 декабря 1895 года.");
        }

        if (film.getDuration() < 0) {
            log.error("Продолжительность отрицательное письмо: {}", film.getDuration());
            throw new ValidationException(400, "Продолжительность фильма не может быть отрицательным числом.");
        }
    }
}
