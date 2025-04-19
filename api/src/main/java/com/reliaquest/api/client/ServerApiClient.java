package com.reliaquest.api.client;

import com.reliaquest.api.dto.*;

import java.util.List;

/**
 * Interface for Server API Client
 * This interface defines the methods to interact with the server API.
 */
public interface ServerApiClient {

    /**
     * Get all employees
     * @return List of EmployeeDTO
     */
    List<EmployeeDTO> getAllEmployee();

    /**
     * Get employee by id
     * @param id Employee id
     * @return EmployeeDTO of the employee searched by id
     */
    EmployeeDTO getEmployeeById(String id);

    /**
     * Create new employee
     * @param createEmployeeRequestDTO Request DTO to create new employee
     * @return SingleEmployeeResponseDTO of the created employee
     */
    SingleEmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO createEmployeeRequestDTO);

    /**
     * Delete employee by id
     * @param deleteEmployeeRequestDTO Request DTO to delete employee
     * @return DeleteEmployeeResponseDTO of the deleted employee
     */
    DeleteEmployeeResponseDTO deleteEmployeeByName(DeleteEmployeeRequestDTO deleteEmployeeRequestDTO);
}
