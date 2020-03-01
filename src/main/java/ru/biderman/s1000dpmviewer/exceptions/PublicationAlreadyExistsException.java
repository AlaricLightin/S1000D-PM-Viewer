package ru.biderman.s1000dpmviewer.exceptions;

public class PublicationAlreadyExistsException extends CustomBadRequestException {
    @Override
    public int getErrorCode() {
        return ErrorCodes.PUBLICATION_ALREADY_EXISTS;
    }

    @Override
    public String getMessage() {
        return "Publication with same code and issue and language is already exists.";
    }
}
