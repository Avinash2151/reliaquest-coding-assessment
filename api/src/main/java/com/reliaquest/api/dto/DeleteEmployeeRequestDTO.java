package com.reliaquest.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class DeleteEmployeeRequestDTO {
    private String name;

    @Override
    public String toString() {
        return "DeleteEmployeeRequestDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
