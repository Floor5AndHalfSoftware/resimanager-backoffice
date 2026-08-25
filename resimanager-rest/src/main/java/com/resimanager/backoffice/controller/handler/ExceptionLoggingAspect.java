package com.resimanager.backoffice.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import com.resimanager.backoffice.exception.ServiceException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {
    @AfterThrowing(pointcut = "execution(* com.resimanager.backoffice.controller.*.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        String method = joinPoint.getSignature().toShortString();
        if (ex instanceof ServiceException se) {
            log.warn("ServiceException in {}: [{}] {}", method, se.getCode(), se.getMessage());
        } else {
            log.error("Unexpected exception in {}: {} - {}", method, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }}
