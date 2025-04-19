package com.reliaquest.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class DeleteEmployeeResponseDTO {
    private Boolean data;
    private String status;

    @Override
    public String toString() {
        return "DeleteEmployeeResponseDTO{" +
                "data=" + data +
                ", status='" + status + '\'' +
                '}';
    }
}
