package com.reliaquest.api.client;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.ExternalServiceException;
import com.reliaquest.api.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerApiClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ServerApiClientImpl serverApiClient;

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
        EmployeeResponseDTO responseDTO = EmployeeResponseDTO.builder().data(mockEmployees).build();
        when(restTemplate.getForObject("/employee", EmployeeResponseDTO.class)).thenReturn(responseDTO);

        List<EmployeeDTO> employees = serverApiClient.getAllEmployee();

        assertEquals(2, employees.size());
        assertEquals(mockEmployees, employees);
        verify(restTemplate, times(1)).getForObject("/employee", EmployeeResponseDTO.class);
    }

    @Test
    void testGetAllEmployee_ExternalServiceException() {
        when(restTemplate.getForObject("/employee", EmployeeResponseDTO.class))
                .thenThrow(new RestClientException("Service unavailable"));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class,
                () -> serverApiClient.getAllEmployee());

        assertEquals("Failed to connect to Employee Service", exception.getMessage());
        verify(restTemplate, times(1)).getForObject("/employee", EmployeeResponseDTO.class);
    }

    @Test
    void testGetEmployeeById() {
        EmployeeDTO mockEmployee = mockEmployees.get(0);
        SingleEmployeeResponseDTO responseDTO = SingleEmployeeResponseDTO.builder().data(mockEmployee).build();
        when(restTemplate.getForObject("/employee/" + mockEmployee.getId(), SingleEmployeeResponseDTO.class))
                .thenReturn(responseDTO);

        EmployeeDTO employee = serverApiClient.getEmployeeById(mockEmployee.getId().toString());

        assertEquals(mockEmployee, employee);
        verify(restTemplate, times(1)).getForObject("/employee/" + mockEmployee.getId(), SingleEmployeeResponseDTO.class);
    }

    @Test
    void testGetEmployeeById_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        when(restTemplate.getForObject("/employee/" + nonExistentId, SingleEmployeeResponseDTO.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> serverApiClient.getEmployeeById(nonExistentId));

        assertEquals("Employee not found with id: " + nonExistentId, exception.getMessage());
        verify(restTemplate, times(1)).getForObject("/employee/" + nonExistentId, SingleEmployeeResponseDTO.class);
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

        SingleEmployeeResponseDTO responseDTO = SingleEmployeeResponseDTO.builder().data(createdEmployee).build();
        when(restTemplate.postForEntity(eq("/employee"), any(), eq(SingleEmployeeResponseDTO.class)))
                .thenReturn(new ResponseEntity<>(responseDTO, HttpStatus.CREATED));

        SingleEmployeeResponseDTO result = serverApiClient.createEmployee(requestDTO);

        assertEquals(createdEmployee, result.getData());
        verify(restTemplate, times(1)).postForEntity(eq("/employee"), any(), eq(SingleEmployeeResponseDTO.class));
    }

    @Test
    void testCreateEmployee_InvalidRequest() {
        CreateEmployeeRequestDTO requestDTO = CreateEmployeeRequestDTO.builder()
                .name("")
                .salary(0)
                .age(15)
                .title("")
                .build();

        when(restTemplate.postForEntity(eq("/employee"), any(), eq(SingleEmployeeResponseDTO.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> serverApiClient.createEmployee(requestDTO));

        assertTrue(exception.getMessage().contains("Failed to create employee"));
        verify(restTemplate, times(1)).postForEntity(eq("/employee"), any(), eq(SingleEmployeeResponseDTO.class));
    }

    @Test
    void testDeleteEmployeeByName() {
        DeleteEmployeeRequestDTO requestDTO = DeleteEmployeeRequestDTO.builder().name("Employee 1").build();
        DeleteEmployeeResponseDTO responseDTO = DeleteEmployeeResponseDTO.builder()
                .data(true)
                .status("Employee deleted successfully")
                .build();

        when(restTemplate.exchange(eq("/employee"), eq(HttpMethod.DELETE), any(), eq(DeleteEmployeeResponseDTO.class), anyString()))
                .thenReturn(new ResponseEntity<>(responseDTO, HttpStatus.OK));

        DeleteEmployeeResponseDTO result = serverApiClient.deleteEmployeeByName(requestDTO);

        assertTrue(result.getData());
        assertEquals("Employee deleted successfully", result.getStatus());
        verify(restTemplate, times(1)).exchange(eq("/employee"), eq(HttpMethod.DELETE), any(), eq(DeleteEmployeeResponseDTO.class), anyString());
    }

    @Test
    void testDeleteEmployeeByName_NotFound() {
        DeleteEmployeeRequestDTO requestDTO = DeleteEmployeeRequestDTO.builder().name("NonExistent").build();

        when(restTemplate.exchange(eq("/employee"), eq(HttpMethod.DELETE), any(), eq(DeleteEmployeeResponseDTO.class), anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> serverApiClient.deleteEmployeeByName(requestDTO));

        verify(restTemplate, times(1)).exchange(eq("/employee"), eq(HttpMethod.DELETE), any(), eq(DeleteEmployeeResponseDTO.class), anyString());
    }
}
