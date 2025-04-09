package com.main.project.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class DuplicateException extends RuntimeException {
    private final List<String> details;

    public DuplicateException(List<String> details) {
        super("VALIDATION_ERROR");
        this.details = details;
    }
}
