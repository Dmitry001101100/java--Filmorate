package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

    private final int status;

    public ValidationException(int status, String message) {
        super(message);
        this.status = status;
    }

}
