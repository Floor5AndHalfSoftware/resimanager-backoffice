package com.resimanager.backoffice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@ToString
@NoArgsConstructor(force = true)
public class HttpErrorInfoJson {

    private final String timestamp;
    private final String path;
    private final HttpStatus httpStatus;
    private final String message;

    public HttpErrorInfoJson(HttpStatus httpStatus, String path, String message) {
        this.timestamp = ZonedDateTime.now().toString();
        this.path = path;
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
