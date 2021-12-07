package ru.bcs.creditmarkt.strapi.exception;

public class FileFormatException extends RuntimeException {
    public FileFormatException(String message) {
        super(message);
    }
}
