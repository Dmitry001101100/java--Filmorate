package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController = new UserController();

    User user = new User(1L, "email@email.com", "login12", "Дмитрий", LocalDate.of(2000, 12, 21));

    @Test
    void getUser() { // проверка полного вывода
        User user2 = new User(2L, "email2@email.com", "login123", "Кирилл", LocalDate.of(1998, 12, 21));
        User user3 = new User(3L, "emai3@email.com", "login123", "Иван", LocalDate.of(2003, 12, 21));

        userController.saveUser(user);
        userController.saveUser(user2);
        userController.saveUser(user3);

        List<User> users = userController.getUser(); /// проработать сравнение через этот список

        assertEquals(user.toString(), users.getFirst().toString(), "toString() user не совпадает");
        assertEquals(user.hashCode(), users.getFirst().hashCode(), "hashCode() фильмов не совпадает");

        assertEquals(user2.toString(), users.get(1).toString(), "toString() user не совпадает");
        assertEquals(user2.hashCode(), users.get(1).hashCode(), "hashCode() фильмов не совпадает");

        assertEquals(user3.toString(), users.get(2).toString(), "toString() user не совпадает");
        assertEquals(user3.hashCode(), users.get(2).hashCode(), "hashCode() фильмов не совпадает");

        userController.clearUsers();
    }

    @Test
    void saveValidateUser() { // валидативное сохранение и обновление пользователя
        userController.saveUser(user);

        assertEquals(user.toString(), userController.getUser().getFirst().toString(), "toString() user не совпадает");
        assertEquals(user.hashCode(), userController.getUser().getFirst().hashCode(), "hashCode() фильмов не совпадает");

        user.setLogin("efe1"); // изменяем пару полей
        user.setEmail("millo@gmail.com");

        userController.updateUser(user);

        assertEquals(user.toString(), userController.getUser().getFirst().toString(), "toString() user не совпадает");
        assertEquals(user.hashCode(), userController.getUser().getFirst().hashCode(), "hashCode() фильмов не совпадает");
        userController.clearUsers();
    }

    @Test
    void validateEmail() { // проверка ошибок валидации электронной почты при сохранении и перезаписи
        user.setEmail(null); // поле email == null
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.saveUser(user)
        );

        assertEquals("Имейл должен быть указан.", exception.getMessage()); // проверка исключения при сохранении фильма

        user.setEmail(" "); // поле email состоит только из пробелов
        ValidationException exception1 = assertThrows(ValidationException.class, () -> userController.saveUser(user));

        assertEquals("Имейл должен быть указан.", exception1.getMessage()); // проверка исключения при сохранении фильма

        user.setEmail("fjij@gmail.com");// изменяем название фильма для валидации и сохраняем
        userController.saveUser(user);

        user.setEmail("uhdfuhu.ru"); // поле email без @

        ValidationException exception2 = assertThrows(ValidationException.class, () -> userController.updateUser(user));

        assertEquals("Имейл должен содержать символ `@`", exception2.getMessage()); // проверка исключения при изменении фильма
        userController.clearUsers();


    }

    // логин не может быть пустым и содержать пробелы

    @Test
    void validateLoginUser() { // ошибки валидативности во время сохранения и обновления пользователя
        user.setLogin("log in");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.saveUser(user)); // при добавлении пользователя
        assertEquals("Логин содержит пробел.", exception.getMessage()); // проверка исключения при сохранении фильма

        user.setLogin("login");
        userController.saveUser(user);

        user.setLogin(null);
        ValidationException exception1 = assertThrows(ValidationException.class, () -> userController.updateUser(user)); // при изменении пользователя
        assertEquals("Логин должен быть указан.", exception1.getMessage()); // проверка исключения при сохранении фильма

        userController.clearUsers();
    }

    // если имя пустое в таком случае будет использоваться логин
    @Test
    void validateNameUser() { // при сохранении
        user.setName(null);
        userController.saveUser(user);
        assertEquals(user.getLogin(), userController.getUser(1L).getName(), "имена не совпадают");
        userController.clearUsers();
    }

    @Test
    void validateNameUpdateUser() { // при обновлении
        userController.saveUser(user);
        user.setName(null);
        userController.updateUser(user);
        assertEquals(user.getLogin(), userController.getUser(1L).getName(), "имена не совпадают");
        userController.clearUsers();
    }

    @Test
    void validateBirthdayUser() { // дата рождения не может быть в будущем при сохранении и обновлении
        user.setBirthday(LocalDate.of(2025, 7, 11));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.saveUser(user));
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage()); // при сохранении

        user.setBirthday(LocalDate.of(2025, 1, 11));
        userController.saveUser(user);

        user.setBirthday(LocalDate.of(2025, 12, 11));

        ValidationException exception1 = assertThrows(ValidationException.class, () -> userController.updateUser(user));
        assertEquals("Дата рождения не может быть в будущем.", exception1.getMessage()); // при обновлении

        userController.clearUsers();
    }
}