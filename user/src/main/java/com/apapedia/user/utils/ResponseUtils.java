package com.apapedia.user.utils;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.apapedia.user.dto.response.BaseResponse;

@Component
public class ResponseUtils {
    public String getBindingErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessages = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            errorMessages
                    .append(error.getDefaultMessage())
                    .append("\n");
        }
        return errorMessages.toString();
    }

    public ResponseEntity<?> getForbiddenResponse(String action) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new BaseResponse<>(false, "You are not allowed to " + action));
    }
}
