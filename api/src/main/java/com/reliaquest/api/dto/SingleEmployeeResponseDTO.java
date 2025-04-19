package com.reliaquest.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class SingleEmployeeResponseDTO {
    private EmployeeDTO data;
    private String status;

    @Override
    public String toString() {
        return "SingleEmployeeResponseDTO{" +
                "data=" + data +
                ", status='" + status + '\'' +
                '}';
    }
}
