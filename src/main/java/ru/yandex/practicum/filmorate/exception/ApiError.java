package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiError { // класс обертка для отправки исключения на сервер в формате json

    private final int status;
    private final String message;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
    }

}