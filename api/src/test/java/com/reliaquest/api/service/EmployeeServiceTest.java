package com.reliaquest.api.service;

import com.reliaquest.api.client.ServerApiClient;
import com.reliaquest.api.dto.CreateEmployeeRequestDTO;
import com.reliaquest.api.dto.DeleteEmployeeResponseDTO;
import com.reliaquest.api.dto.EmployeeDTO;
import com.reliaquest.api.dto.SingleEmployeeResponseDTO;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private ServerApiClient serverApiClient;

    @InjectMocks
    private EmployeeService employeeService;

    private List<EmployeeDTO> mockEmployees;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmployees = Arrays.asList(
                EmployeeDTO.builder()
                        .id(UUID.randomUUID())
                        .name("Employee 1")
                        .salary(5000)
                        .age(30)
                        .title("Software Engineer")
                        .build(),
                EmployeeDTO.builder()
                        .id(UUID.randomUUID())
                        .name("Employee 2")
                        .salary(7000)
                        .age(40)
                        .title("Data Analyst")
                        .build()
        );
    }

    @Test
    void testGetAllEmployee() {
        when(serverApiClient.getAllEmployee()).thenReturn(mockEmployees);

        List<EmployeeDTO> employees = employeeService.getAllEmployee();

        assertEquals(2, employees.size());
        assertEquals(mockEmployees, employees);
        verify(serverApiClient, times(1)).getAllEmployee();
    }

    @Test
    void testGetEmployeeById() {
        EmployeeDTO mockEmployee = mockEmployees.get(0);
        when(serverApiClient.getEmployeeById(mockEmployee.getId().toString())).thenReturn(mockEmployee);

        EmployeeDTO employee = employeeService.getEmployeeById(mockEmployee.getId().toString());

        assertEquals(mockEmployee, employee);
        verify(serverApiClient, times(1)).getEmployeeById(mockEmployee.getId().toString());
    }

    @Test
    void testGetEmployeeById_InvalidId() {
        String invalidId = "";
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> employeeService.getEmployeeById(invalidId));

        assertEquals("Employee ID must not be null or empty", exception.getMessage());
        verify(serverApiClient, never()).getEmployeeById(anyString());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        when(serverApiClient.getEmployeeById(nonExistentId))
                .thenThrow(new EmployeeNotFoundException("Employee not found with Id : " + nonExistentId));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(nonExistentId));

        assertEquals("Employee not found with Id : " + nonExistentId, exception.getMessage());
        verify(serverApiClient, times(1)).getEmployeeById(nonExistentId);
    }

    @Test
    void testCreateEmployee() {
        CreateEmployeeRequestDTO requestDTO = CreateEmployeeRequestDTO.builder()
                .name("Employee 1")
                .salary(5000)
                .age(30)
                .title("Software Engineer")
                .build();

        EmployeeDTO createdEmployee = EmployeeDTO.builder()
                .id(UUID.randomUUID())
                .name("Employee 1")
                .salary(5000)
                .age(30)
                .title("Software Engineer")
                .build();

        SingleEmployeeResponseDTO responseDTO = SingleEmployeeResponseDTO.builder()
                .data(createdEmployee)
                .build();

        when(serverApiClient.createEmployee(requestDTO)).thenReturn(responseDTO);

        EmployeeDTO result = employeeService.createEmployee(requestDTO);

        assertEquals(createdEmployee, result);
        verify(serverApiClient, times(1)).createEmployee(requestDTO);
    }

    @Test
    void testCreateEmployee_InvalidRequest() {
        CreateEmployeeRequestDTO invalidRequest = CreateEmployeeRequestDTO.builder()
                .name("abc")
                .salary(0)
                .age(15)
                .title("xyz")
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.createEmployee(invalidRequest));

        assertNotNull(exception.getMessage());
    }

    @Test
    void testDeleteEmployeeById() {
        EmployeeDTO mockEmployee = mockEmployees.get(0);
        when(serverApiClient.getEmployeeById(mockEmployee.getId().toString())).thenReturn(mockEmployee);
        when(serverApiClient.deleteEmployeeByName(any())).thenReturn(DeleteEmployeeResponseDTO.builder().data(true).status("Employee deleted successfully").build());

        String result = employeeService.deleteEmployeeById(mockEmployee.getId().toString());

        assertEquals(mockEmployee.getName(), result);
        verify(serverApiClient, times(1)).getEmployeeById(mockEmployee.getId().toString());
        verify(serverApiClient, times(1)).deleteEmployeeByName(any());
    }

    @Test
    void testDeleteEmployeeById_InvalidId() {
        String invalidId = "";
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> employeeService.deleteEmployeeById(invalidId));

        assertEquals("Employee ID must not be null or empty", exception.getMessage());
        verify(serverApiClient, never()).getEmployeeById(anyString());
    }

    @Test
    void testDeleteEmployeeById_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        when(serverApiClient.getEmployeeById(nonExistentId))
                .thenThrow(new EmployeeNotFoundException("Employee not found with Id : " + nonExistentId));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployeeById(nonExistentId));

        assertEquals("Employee not found with Id : " + nonExistentId, exception.getMessage());
        verify(serverApiClient, times(1)).getEmployeeById(nonExistentId);
    }
}
