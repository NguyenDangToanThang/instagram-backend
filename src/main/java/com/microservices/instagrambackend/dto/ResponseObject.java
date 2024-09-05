package com.microservices.instagrambackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject<T> {
    private String code;
    private String message;
    private T data;

    public ResponseObject(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> ResponseObject<T> success(T data) {
        return new ResponseObject<>("200", "Success", data);
    }

    public static <T> ResponseObject<T> success() {
        return new ResponseObject<>("200", "Success");
    }

    public static <T> ResponseObject<T> error(String code, String message) {
        return new ResponseObject<>(code, message);
    }

    public static <T> ResponseObject<T> error(String code, String message, T data) {
        return new ResponseObject<>(code, message, data);
    }

}
