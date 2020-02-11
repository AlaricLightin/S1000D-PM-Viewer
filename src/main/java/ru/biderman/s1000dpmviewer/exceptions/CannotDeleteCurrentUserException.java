package ru.biderman.s1000dpmviewer.exceptions;

public class CannotDeleteCurrentUserException extends CustomBadRequestException {
    @Override
    public int getErrorCode() {
        return ErrorCodes.CANNOT_DELETE_CURRENT_USER;
    }

    @Override
    public String getMessage() {
        return "Cannot delete current user.";
    }
}
