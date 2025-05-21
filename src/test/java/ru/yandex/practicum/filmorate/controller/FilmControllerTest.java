package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class FilmControllerTest {
    FilmController filmController = new FilmController();

    Film film = new Film(1L, "фильм1", "Описание фильма1", LocalDate.of(1995, 12, 13), 100);

    @Test
    void getFilms() {  // вывод фильмов
        Film film2 = new Film(2L, "фильм2", "Описание фильма2", LocalDate.of(1995, 12, 13), 100);
        Film film3 = new Film(3L, "фильм3", "Описание фильма3", LocalDate.of(1995, 12, 13), 120);

        filmController.saveFilm(film);
        filmController.saveFilm(film2);
        filmController.saveFilm(film3);

        List<Film> films = filmController.getFilms();

        assertEquals(film.toString(), films.getFirst().toString(), "toString() фильма1 не совпадает");
        assertEquals(film.hashCode(), films.getFirst().hashCode(), "hashCode() фильмов не совпадает");

        assertEquals(film2.toString(), films.get(1).toString(), "toString() фильма1 не совпадает");
        assertEquals(film2.hashCode(), films.get(1).hashCode(), "hashCode() фильмов не совпадает");

        assertEquals(film3.toString(), films.get(2).toString(), "toString() фильма1 не совпадает");
        assertEquals(film3.hashCode(), films.get(2).hashCode(), "hashCode() фильмов не совпадает");

        filmController.clearFilms();
    }

    @Test
    void validateUpdate() throws NotFoundException { // валидативное обновление фильма
        filmController.saveFilm(film); // сохраняем фильм

        Film film1 = filmController.getFilm(1L);
        assertEquals(film.toString(), film1.toString(), "toString() фильмов не совпадает");
        assertEquals(film.hashCode(), film1.hashCode(), "hashCode() фильмов не совпадает");

        Film film2 = new Film(1L, "фильм2", "Описание фильма2", LocalDate.of(1995, 12, 13), 120);

        filmController.updateFilm(film2);

        assertEquals(film2.toString(), filmController.getFilm(1L).toString(), "toString() фильмов не совпадает");
        assertEquals(film2.hashCode(), filmController.getFilm(1L).hashCode(), "hashCode() фильмов не совпадает");

        filmController.clearFilms();

    }

    @Test
    void saveValidate() throws NotFoundException { // сохранение фильма без нарушений валидации

        filmController.saveFilm(film); // при сохранении автоматом меняется id

        Film film1 = filmController.getFilm(1L);
        assertEquals(film.toString(), film1.toString(), "toString() фильмов не совпадает");
        assertEquals(film.hashCode(), film1.hashCode(), "hashCode() фильмов не совпадает");
        filmController.clearFilms();
    }

    @Test
    void validateNameFilm() { // проверка валидации имени фильма при сохранении и перезаписи.
        film.setName(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.saveFilm(film));
        assertEquals("Название фильма не может быть пустым.", exception.getMessage()); // проверка исключения при сохранении фильма

        film.setName("Фильм");// изменяем название фильма для валидации и сохраняем
        filmController.saveFilm(film); // при сохранении автоматом меняется id

        film.setName(null); // перед обновлением имя на null
        ValidationException exception1 = assertThrows(ValidationException.class, () -> filmController.updateFilm(film));

        assertEquals("Название фильма не может быть пустым.", exception1.getMessage()); // проверка исключения при изменении фильма
        filmController.clearFilms();
    }

    @Test
    void validateDescriptionFilm() throws NotFoundException { // проверка валидации описания фильма при сохранении и перезаписи.
        String noValidDesc = "12121212121212121212" + // 10 строк по 20 символов и одна строка с 1 символом(суммарно 201 символ)
                "12121212121212121212" +
                "12121212121212121212" +
                "12121212121212121212" +
                "12121212121212121212" +
                "12121212121212121212" +
                "12121212121212121212" +
                "12121212121212121212" +
                "12121212121212121212" +
                "12121212121212121212" + "1";
        film.setDescription(noValidDesc);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.saveFilm(film));

        assertEquals("Описание не может быть больше 200 символов.", exception.getMessage()); // проверка исключения при сохранении фильма

        String validDesc = noValidDesc.substring(1); // создаем валидативное описание длинной в 200 симоволов

        film.setDescription(validDesc);// изменяем описание фильма для валидации и сохраняем
        filmController.saveFilm(film); // при сохранении автоматом меняется id

        System.out.println(filmController.getFilm(1L));

        film.setDescription(noValidDesc); // перед меняем описание фильма для ошибки валидации

        ValidationException exception1 = assertThrows(ValidationException.class, () -> filmController.updateFilm(film));

        assertEquals("Описание не может быть больше 200 символов.", exception1.getMessage()); // проверка исключения при изменении фильма
        filmController.clearFilms();
    }

    @Test
    void validateReleaseDateFilm() { // проверка валидации даты релиза фильма при сохранении и перезаписи.
        film.setReleaseDate(LocalDate.of(1985, 12, 27));
        // для прохождения валидации дата релиза фильма должна быть не раньше чем 28.12.1985
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.saveFilm(film));

        assertEquals("Дата релиза — не раньше 28 декабря 1895 года.", exception.getMessage()); // проверка исключения при сохранении фильма


        film.setReleaseDate(LocalDate.of(1985, 12, 28));// изменяем дату релиза фильма для валидации и сохраняем
        filmController.saveFilm(film); // при сохранении автоматом меняется id

        film.setReleaseDate(LocalDate.of(1985, 12, 27)); // обновляем дату релиза раньше для нарушения валидации
        ValidationException exception1 = assertThrows(ValidationException.class, () -> filmController.updateFilm(film));

        assertEquals("Дата релиза — не раньше 28 декабря 1895 года.", exception1.getMessage()); // проверка исключения при изменении фильма
        filmController.clearFilms();
    }

    @Test
    void validateDurationFilm() { // проверка валидации продолжительности фильма при сохранении и перезаписи.
        film.setDuration(-1); // ставим продолжительность фильма -1 минута
        // для прохождения валидации продолжительность фильма не должна быть отрицательным числолм
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.saveFilm(film));

        assertEquals("Продолжительность фильма не может быть отрицательным числом.", exception.getMessage()); // проверка исключения при сохранении фильма

        film.setDuration(0);// изменяем продолжительность фильма на 0 минут для валидации и сохраняем
        filmController.saveFilm(film); // при сохранении автоматом меняется id

        film.setDuration(-1); // ставим продолжительность фильма -1 минута и обновляем
        ValidationException exception1 = assertThrows(ValidationException.class, () -> filmController.updateFilm(film));

        assertEquals("Продолжительность фильма не может быть отрицательным числом.", exception1.getMessage()); // проверка исключения при изменении фильма
        filmController.clearFilms();
    }

}
