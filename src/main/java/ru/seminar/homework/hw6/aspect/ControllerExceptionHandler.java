package ru.seminar.homework.hw6.aspect;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.seminar.homework.hw6.dto.ExceptionDto;
import ru.seminar.homework.hw6.dto.ValidationErrorDto;
import ru.seminar.homework.hw6.dto.ValidationErrorsDto;
import ru.seminar.homework.hw6.exception.ExistsException;
import ru.seminar.homework.hw6.exception.NotFoundException;
import ru.seminar.homework.hw6.exception.ServiceException;
import ru.seminar.homework.hw6.exception.ServiceValidationException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Log4j2
@RestControllerAdvice
public class ControllerExceptionHandler {
    private static ResponseEntity<ExceptionDto> collectResponse(HttpStatus code, String message) {
        return ResponseEntity
                .status(code)
                .body(new ExceptionDto()
                        .code(String.valueOf(code.value()))
                        .message(message));
    }

    private static ResponseEntity<ValidationErrorsDto> collectResponse(List<ValidationErrorDto> errors) {
        return ResponseEntity
                .badRequest()
                .body(new ValidationErrorsDto().errors(errors));
    }

    private static ResponseEntity<ValidationErrorsDto> collectResponse(ValidationErrorDto... errors) {
        return collectResponse(List.of(errors));
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException ex) {
        return collectResponse(NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ExistsException.class)
    ResponseEntity<ExceptionDto> handleExistsException(ExistsException ex) {
        return collectResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    ResponseEntity<ExceptionDto> handle(ServiceException ex) {
        return handleUnknownError(ex);
    }

    @ExceptionHandler(ServiceValidationException.class)
    ResponseEntity<ExceptionDto> handleServiceValidationException(ServiceValidationException ex) {
        return collectResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ExceptionDto> handleUnknownError(RuntimeException ex) {
        log.warn(ex);
        return collectResponse(INTERNAL_SERVER_ERROR, "Service unavailable.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ExceptionDto> handleEmptyRequest() {
        return collectResponse(BAD_REQUEST, "Empty request.");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorsDto> handleInvalidRequest(ConstraintViolationException ex) {
        List<ValidationErrorDto> list = ex.getConstraintViolations().stream()
                .map(cv -> {
                    String propertyPath = cv.getPropertyPath().toString();
                    return new ValidationErrorDto()
                            .field(propertyPath.substring(propertyPath.lastIndexOf(".") + 1))
                            .value(cv.getInvalidValue().toString())
                            .message(cv.getMessage());
                })
                .toList();

        return collectResponse(list);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ValidationErrorsDto> handleMissingParamException(MissingServletRequestParameterException ex) {
        var error = new ValidationErrorDto()
                .field(ex.getParameterName())
                .message("Empty parameter");

        return collectResponse(error);
    }
}
