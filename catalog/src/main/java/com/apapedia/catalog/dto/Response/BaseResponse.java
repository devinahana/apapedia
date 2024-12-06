package com.apapedia.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private Boolean isSuccess;
    private String message;
    private T content;

    public BaseResponse(Boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}