package com.reliaquest.api.dto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class EmployeeResponseDTO {
    private List<EmployeeDTO> data;
    private String status;

    @Override
    public String toString() {
        return "EmployeeResponseDTO{" +
                "data=" + data +
                ", status='" + status + '\'' +
                '}';
    }
}

