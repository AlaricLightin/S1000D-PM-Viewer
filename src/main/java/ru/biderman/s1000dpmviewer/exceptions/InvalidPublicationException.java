package ru.biderman.s1000dpmviewer.exceptions;

public class InvalidPublicationException extends CustomBadRequestException {
    @Override
    public int getErrorCode() {
        return ErrorCodes.INVALID_PUBLICATION;
    }

    @Override
    public String getMessage() {
        return "XML file is not valid publication.";
    }
}
