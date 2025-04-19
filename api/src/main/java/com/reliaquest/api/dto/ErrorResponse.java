package com.reliaquest.api.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String error;
    private int code;
    private String path;

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", status='" + error + '\'' +
                ", code=" + code + '\'' +
                ", path='" + path +
                '}';
    }
}
