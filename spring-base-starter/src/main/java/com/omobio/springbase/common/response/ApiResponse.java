package com.omobio.springbase.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;

    public ApiResponse(String message) {
        this.message = message;
    }
}
