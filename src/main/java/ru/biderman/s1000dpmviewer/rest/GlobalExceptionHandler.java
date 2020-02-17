package ru.biderman.s1000dpmviewer.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.biderman.s1000dpmviewer.exceptions.CustomBadRequestException;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.rest.dto.CustomExceptionDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomExceptionDto badRequestExceptionHandler(CustomBadRequestException e) {
        return CustomExceptionDto.createFromException(e);
    }

    @ExceptionHandler(PublicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void publicationNotFoundHandler() {

    }
}
