package ru.biderman.s1000dpmviewer.exceptions;

abstract public class CustomBadRequestException extends Exception {
    abstract public int getErrorCode();
}
