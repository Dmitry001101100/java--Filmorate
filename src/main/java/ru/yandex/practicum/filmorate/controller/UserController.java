package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 0L;

    @GetMapping
    public List<User> getUser() {
        return new ArrayList<>(users.values());
    }

    public User getUser(Long id) {  // поиск по id для тестов
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    public void clearUsers() {
        users.clear();
        nextId = 0L;
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

        if (user.getId() == 0) {
            log.error("Введен id : {}", 0);
            throw new ValidationException("Для изменения данных требуется указать id.");
        } else {
            if (users.containsKey(user.getId())) {
                log.info("Обновление нового пользователя : {}", user);
                validateUser(user);
                users.put(user.getId(), user);
            } else {
                log.error("Нет такого id: {}", user.getId());
                throw new ValidationException("Пользователь с данным id не найден.");
            }
        }

        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) { // проверяем что имейл не пустой
            log.error("Недопустимое поле имейл: {}", user.getEmail());
            throw new ValidationException("Имейл должен быть указан.");
        } else {
            if (!user.getEmail().contains("@")) { // проверяем содержит ли имейл  @
                log.error("В поле имейл отсутствует символ '@': {}", user.getEmail());
                throw new ValidationException("Имейл должен содержать символ `@`");
            }

            if (user.getLogin() == null || user.getLogin().isBlank()) { // проверяем что логин не пустой
                log.error("Не указан логин: {}", user.getLogin());
                throw new ValidationException("Логин должен быть указан.");
            } else if (user.getLogin().contains(" ")) { // проверяем что логин не содержит пробелов
                log.error("В поле логин недопустимый символ 'пробел': {}", user.getEmail());
                throw new ValidationException("Логин содержит пробел.");
            }

            if (user.getName() == null || user.getName().isBlank()) { // если имя пусто записываем в него логин
                log.info("Изменение поля name на :{}", user.getLogin());
                user.setName(user.getLogin());
            }

            if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) { // проверка не в будущем ли дата рождения
                log.error("Ошибка даты рождения: {}", user.getBirthday());
                throw new ValidationException("Дата рождения не может быть в будущем."); // дата рождения в будущем — некорректно
            }
        }
    }
}
