package ru.biderman.s1000dpmviewer.exceptions;

public class InvalidUsernameException extends CustomBadRequestException {
    @Override
    public int getErrorCode() {
        return ErrorCodes.INVALID_USERNAME;
    }

    @Override
    public String getMessage() {
        return "Invalid username.";
    }
}
