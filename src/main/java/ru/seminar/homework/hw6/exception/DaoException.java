package ru.seminar.homework.hw6.exception;

import lombok.Getter;

@Getter
public class DaoException extends ServiceException {
    /**
     * 0 - null id
     * 1 - io exception
     * 2 - entity with this id no present in storage
     * 3 - illegal argument
     */
    private final int code;

    public DaoException(String message, int code) {
        super(message);
        this.code = code;
    }

    public DaoException(Throwable ex, int code) {
        super(ex);
        this.code = code;
    }
}
