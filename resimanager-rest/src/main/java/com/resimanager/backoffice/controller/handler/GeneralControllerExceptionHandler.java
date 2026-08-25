package com.resimanager.backoffice.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.resimanager.backoffice.dto.HttpErrorInfoJson;
import com.resimanager.backoffice.exception.ServiceException;
import com.resimanager.backoffice.controller.handler.FormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GeneralControllerExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ResponseEntity<HttpErrorInfoJson> handleException(Exception exception, HttpServletRequest request) {
        HttpErrorInfoJson httpErrorInfoJson = FormatUtils.httpErrorInfoFormatted(HttpStatus.INTERNAL_SERVER_ERROR, request, exception);
        log.error(httpErrorInfoJson.toString());
        log.error(Arrays.toString(exception.getStackTrace()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(httpErrorInfoJson);
    }

    @ExceptionHandler(value = {ServiceException.class})
    @ResponseBody
    public ResponseEntity<HttpErrorInfoJson> handleServiceException(ServiceException serviceException, HttpServletRequest request) {
        HttpErrorInfoJson httpErrorInfoJson;

        switch (serviceException.getCode()) {
            case 400 -> {
                httpErrorInfoJson = FormatUtils.httpErrorInfoFormatted(HttpStatus.BAD_REQUEST, request, serviceException);
                log.error(httpErrorInfoJson.toString());
                log.error(Arrays.toString(serviceException.getStackTrace()));
                return new ResponseEntity<>(httpErrorInfoJson, HttpStatus.BAD_REQUEST);
            }
            case 404 -> {
                httpErrorInfoJson = FormatUtils.httpErrorInfoFormatted(HttpStatus.NOT_FOUND, request, serviceException);
                log.error(httpErrorInfoJson.toString());
                log.error(Arrays.toString(serviceException.getStackTrace()));
                return new ResponseEntity<>(httpErrorInfoJson, HttpStatus.NOT_FOUND);
            }
            default -> {
                httpErrorInfoJson = FormatUtils.httpErrorInfoFormatted(HttpStatus.INTERNAL_SERVER_ERROR, request, serviceException);
                log.error(httpErrorInfoJson.toString());
                log.error(Arrays.toString(serviceException.getStackTrace()));
                return new ResponseEntity<>(httpErrorInfoJson, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
