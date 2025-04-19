package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class CreateEmployeeRequestDTO {

    @NotBlank
    @JsonProperty("name")
    private String name;

    @Min(value = 1, message = "Salary must be greater than 0")
    @JsonProperty("salary")
    private int salary;

    @Min(value = 16, message = "Age must be greater than 16")
    @Max(value = 75, message = "Age must be less than 75")
    @JsonProperty("age")
    private int age;

    @NotBlank
    @JsonProperty("title")
    private String title;

    @Override
    public String toString() {
        return "CreateEmployeeRequestDTO{" +
                "name='" + name + '\'' +
                ", salary=" + salary +
                ", age=" + age +
                ", title='" + title + '\'' +
                '}';
    }
}
