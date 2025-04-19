package com.reliaquest.api.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("employee_name")
    private String name;

    @JsonProperty("employee_salary")
    private int salary;

    @JsonProperty("employee_age")
    private int age;

    @JsonProperty("employee_title")
    private String title;

    @JsonProperty("employee_email")
    private String email;

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", age=" + age +
                ", title='" + title + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
