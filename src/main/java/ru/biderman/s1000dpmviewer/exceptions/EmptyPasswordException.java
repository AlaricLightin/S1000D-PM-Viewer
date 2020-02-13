package ru.biderman.s1000dpmviewer.exceptions;

import static ru.biderman.s1000dpmviewer.exceptions.ErrorCodes.EMPTY_PASSWORD;

public class EmptyPasswordException extends CustomBadRequestException {
    @Override
    public int getErrorCode() {
        return EMPTY_PASSWORD;
    }

    @Override
    public String getMessage() {
        return "Password can not be empty.";
    }
}
