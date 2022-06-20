package com.example.Thingspeak.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestResponse<T> {
    private String status;
    private int httpCode;
    private String message;

    private T data;
}
