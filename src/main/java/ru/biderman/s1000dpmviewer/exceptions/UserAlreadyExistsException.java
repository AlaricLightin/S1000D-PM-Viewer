package ru.biderman.s1000dpmviewer.exceptions;

public class UserAlreadyExistsException extends CustomBadRequestException {
    @Override
    public int getErrorCode() {
        return ErrorCodes.USER_ALREADY_EXISTS;
    }

    @Override
    public String getMessage() {
        return "User with same username is already exists.";
    }
}
