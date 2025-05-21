package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 0L;

    //-------------------------------------------------------------------------------------------------------------------
    @GetMapping
    public List<User> getUser() {
        return new ArrayList<>(users.values());
    }

    @GetMapping("/{id}")
    public User getUser(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.error("There is no user with this id: {}", id);
            throw new NotFoundException(404, "Пользователь с id " + id + " не найден.");
        }
    }

    @DeleteMapping
    public void clearUsers() {
        log.info("Clear users.");
        users.clear();
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        log.info("Сохранение нового пользователя : {}", user);
        validateUser(user);
        user.setId(++nextId);
        users.put(user.getId(), user);
        return user;
    }


    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("изменение пользователя пользователя : {}", user);
        if (user.getId() == 0) {
            log.error("Введен id : {}", 0);
            throw new ValidationException(404, "Для изменения данных требуется указать id.");
        } else {
            if (users.containsKey(user.getId())) {
                log.info("Обновление нового пользователя : {}", user);
                validateUser(user);
                users.put(user.getId(), user);
            } else {
                log.error("Нет такого id: {}", user.getId());
                throw new ValidationException(404, "Пользователь с данным id не найден.");
            }
        }

        return user;
    }

    //-----------------------------------------------------------------------------------------------------------------------
    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) { // проверяем что имейл не пустой
            log.error("Недопустимое поле имейл: {}", user.getEmail());
            throw new ValidationException(400, "Имейл должен быть указан.");
        } else {
            if (!user.getEmail().contains("@")) { // проверяем содержит ли имейл  @
                log.error("В поле имейл отсутствует символ '@': {}", user.getEmail());
                throw new ValidationException(400, "Имейл должен содержать символ `@`");
            }
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) { // проверяем что логин не пустой
            log.error("Не указан логин: {}", user.getLogin());
            throw new ValidationException(400, "Логин должен быть указан.");
        } else if (user.getLogin().contains(" ")) { // проверяем что логин не содержит пробелов
            log.error("В поле логин недопустимый символ 'пробел': {}", user.getEmail());
            throw new ValidationException(400, "Логин содержит пробел.");
        }

        if (user.getName() == null || user.getName().isBlank()) { // если имя пусто записываем в него логин
            log.info("Изменение поля name на :{}", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) { // проверка не в будущем ли дата рождения
            log.error("Ошибка даты рождения: {}", user.getBirthday());
            throw new ValidationException(400, "Дата рождения не может быть в будущем."); // дата рождения в будущем — некорректно
        }

    }

}
