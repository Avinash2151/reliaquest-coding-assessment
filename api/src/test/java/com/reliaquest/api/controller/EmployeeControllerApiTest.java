package com.reliaquest.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequestDTO;
import com.reliaquest.api.dto.EmployeeDTO;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerApiTest {

    private final static String BASE_API_URL = "/api/v1/employee";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDTO sampleEmployee;

    @BeforeEach
    void setup() {
        sampleEmployee = EmployeeDTO.builder()
                .id(UUID.randomUUID())
                .name("Employee 1")
                .age(18)
                .salary(10000)
                .build();
    }

    @Test
    void getEmployeeNameFromResponse() throws Exception {
        Mockito.when(employeeService.getAllEmployee()).thenReturn(List.of(sampleEmployee));

        MvcResult result = mockMvc.perform(get(BASE_API_URL))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<EmployeeDTO> employees = objectMapper.readValue(jsonResponse, new TypeReference<List<EmployeeDTO>>() {
        });

        String employeeName = employees.get(0).getName();
        assertEquals("Employee 1", employeeName);
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        Mockito.when(employeeService.getEmployeeById(sampleEmployee.getId().toString())).thenReturn(sampleEmployee);

        MvcResult result = mockMvc.perform(get(BASE_API_URL + "/" + sampleEmployee.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        EmployeeDTO employee = objectMapper.readValue(jsonResponse, new TypeReference<EmployeeDTO>() {
        });

        String employeeName = employee.getName();
        assertEquals("Employee 1", employeeName);
    }

    @Test
    void getEmployeeById_shouldReturn404IfNotFound() throws Exception {
        UUID nonExistingId = UUID.randomUUID();
        Mockito.when(employeeService.getEmployeeById(nonExistingId.toString()))
                .thenThrow(new EmployeeNotFoundException("Employee not found with id : " + nonExistingId));

        mockMvc.perform(get(BASE_API_URL + nonExistingId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getEmployeesByNameSearch_shouldReturnMatchingEmployees() throws Exception {
        Mockito.when(employeeService.getEmployeesByNameSearch("Emp"))
                .thenReturn(List.of(sampleEmployee));

        MvcResult result = mockMvc.perform(get(BASE_API_URL + "/search/Emp"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<EmployeeDTO> employees = objectMapper.readValue(jsonResponse, new TypeReference<List<EmployeeDTO>>() {
        });

        String employeeName = employees.get(0).getName();
        assertEquals("Employee 1", employeeName);
    }

    @Test
    void getEmployeesByNameSearch_shouldReturnEmptyListIfNoMatch() throws Exception {
        Mockito.when(employeeService.getEmployeesByNameSearch("notfound")).thenReturn(List.of());

        MvcResult result = mockMvc.perform(get(BASE_API_URL + "/search/notfound"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<EmployeeDTO> employees = objectMapper.readValue(jsonResponse, new TypeReference<List<EmployeeDTO>>() {
        });

        assertEquals(0, employees.size());
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee() throws Exception {
        CreateEmployeeRequestDTO request = CreateEmployeeRequestDTO.builder()
                .name("Employee 1")
                .age(18)
                .salary(10000)
                .build();

        Mockito.when(employeeService.createEmployee(any(CreateEmployeeRequestDTO.class)))
                .thenReturn(sampleEmployee);

        MvcResult result = mockMvc.perform(post(BASE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        EmployeeDTO employee = objectMapper.readValue(jsonResponse, new TypeReference<EmployeeDTO>() {
        });

        String employeeName = employee.getName();
        assertEquals("Employee 1", employeeName);
    }

    @Test
    void deleteEmployeeById_shouldReturnDeletedName() throws Exception {
        Mockito.when(employeeService.deleteEmployeeById(sampleEmployee.getId().toString())).thenReturn("Employee 1");

        mockMvc.perform(delete(BASE_API_URL + "/" + sampleEmployee.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee 1"));
    }

    @Test
    void deleteEmployeeById_shouldHandleNotFound() throws Exception {
        Mockito.when(employeeService.deleteEmployeeById("999"))
                .thenReturn("Employee not found");

        mockMvc.perform(delete( BASE_API_URL + "/999"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee not found"));
    }
}
