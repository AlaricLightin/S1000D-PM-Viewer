package ru.biderman.s1000dpmviewer.rest.dto;

import lombok.Getter;
import ru.biderman.s1000dpmviewer.exceptions.CustomBadRequestException;

@Getter
public class CustomExceptionDto {
    private final int errorCode;
    private final String message;

    private CustomExceptionDto(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static CustomExceptionDto createFromException(CustomBadRequestException exception) {
        return new CustomExceptionDto(exception.getErrorCode(), exception.getMessage());
    }
}
