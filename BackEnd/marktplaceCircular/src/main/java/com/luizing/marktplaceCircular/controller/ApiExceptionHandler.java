package com.luizing.marktplaceCircular.controller;

import com.luizing.marktplaceCircular.dtos.ApiErroDto;
import com.luizing.marktplaceCircular.validation.DadosInvalidosException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ApiErroDto> tratarDadosInvalidos(DadosInvalidosException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErroDto(exception.getMessage()));
    }
}
