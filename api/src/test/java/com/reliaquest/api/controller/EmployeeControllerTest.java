package com.reliaquest.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequestDTO;
import com.reliaquest.api.dto.EmployeeDTO;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmployeeControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    List<EmployeeDTO> mockEmployees;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmployees = Arrays.asList(
                EmployeeDTO.builder()
                        .name("Employee 1")
                        .id(UUID.randomUUID())
                        .salary(5000)
                        .age(35)
                        .title("Software Developer")
                        .email("employee1@reliaquest.com")
                        .build(),

                EmployeeDTO.builder()
                        .name("Employee 2")
                        .id(UUID.randomUUID())
                        .salary(70000)
                        .age(50)
                        .title("Data Analyst")
                        .email("employee2@reliaquest.com")
                        .build()
        );
    }

    @Test
    void testGetAllEmployees() {
        when(employeeService.getAllEmployee()).thenReturn(mockEmployees);

        ResponseEntity<List<EmployeeDTO>> response = employeeController.getAllEmployees();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockEmployees, response.getBody());
        verify(employeeService, times(1)).getAllEmployee();
    }

    @Test
    void testGetEmployeeById() {
        when(employeeService.getEmployeeById(mockEmployees.get(0).getId().toString())).thenReturn(mockEmployees.get(0));

        ResponseEntity<EmployeeDTO> response = employeeController.getEmployeeById(mockEmployees.get(0).getId().toString());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockEmployees.get(0), response.getBody());
        verify(employeeService, times(1)).getEmployeeById(mockEmployees.get(0).getId().toString());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        when(employeeService.getEmployeeById(nonExistentId))
                .thenThrow(new EmployeeNotFoundException("Employee not found with Id : " + nonExistentId));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> employeeController.getEmployeeById(nonExistentId));

        assertEquals("Employee not found with Id : " + nonExistentId, exception.getMessage());
        verify(employeeService, times(1)).getEmployeeById(nonExistentId);
    }

    @Test
    void testCreateEmployee() {
        CreateEmployeeRequestDTO requestDTO = CreateEmployeeRequestDTO.builder()
                .name("Employee 1")
                .age(45)
                .salary(30000)
                .title("Automation Testing")
                .build();

        EmployeeDTO createdEmployee = EmployeeDTO.builder()
                .id(UUID.randomUUID())
                .name("Employee 1")
                .age(45)
                .salary(30000)
                .title("Automation Testing")
                .email("employee1@reliaquest.com")
                .build();

        when(employeeService.createEmployee(requestDTO)).thenReturn(createdEmployee);

        ResponseEntity<EmployeeDTO> response = employeeController.createEmployee(requestDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(createdEmployee.getName(), response.getBody().getName());
        assertEquals(createdEmployee.getSalary(), response.getBody().getSalary());
        assertEquals(createdEmployee.getId(), response.getBody().getId());
        verify(employeeService, times(1)).createEmployee(any(CreateEmployeeRequestDTO.class));
    }

    @Test
    void testDeleteEmployeeById() {
        EmployeeDTO mockEmployee = mockEmployees.get(1);
        when(employeeService.deleteEmployeeById(mockEmployee.getId().toString())).thenReturn(mockEmployee.getName());

        ResponseEntity<String> response = employeeController.deleteEmployeeById(mockEmployee.getId().toString());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockEmployee.getName(), response.getBody());
        verify(employeeService, times(1)).deleteEmployeeById(mockEmployee.getId().toString());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        List<String> mockNames = mockEmployees.stream().map(EmployeeDTO::getName).toList();
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(mockNames);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockNames, response.getBody());
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }
}
