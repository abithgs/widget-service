package com.miro.widget.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExceptionResponse {
    private LocalDateTime timestamp;

    private String message;

    private String details;
}