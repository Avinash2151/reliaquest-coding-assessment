package com.reliaquest.api.controller;

import com.reliaquest.api.dto.CreateEmployeeRequestDTO;
import com.reliaquest.api.dto.EmployeeDTO;
import com.reliaquest.api.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<EmployeeDTO, CreateEmployeeRequestDTO> {

    private final EmployeeService employeeService;

    /**
     * Get all employees
     * @return List of EmployeeDTO
     *
     * TODO: Pagination can be added in future to fetch employees in chunks
     */
    @Override
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return new ResponseEntity<>(employeeService.getAllEmployee(), HttpStatus.OK);
    }

    /**
     * Get employees by name search
     * @param searchString String to search for in employee names
     * @return List of EmployeeDTO with names containing the search string
     *
     * Note - Special feature has been added to sort the employees in such a way that,
     * the employees whose names start with the search string will be at the top of the list
     * and the rest of the employees whose name contains the search string will be sorted alphabetically.
     */
    @Override
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByNameSearch(String searchString) {
        return new ResponseEntity<>(employeeService.getEmployeesByNameSearch(searchString), HttpStatus.OK);
    }

    /**
     * Get employee by id
     * @param id Employee id
     * @return EmployeeDTO of the employee searched by id
     */
    @Override
    public ResponseEntity<EmployeeDTO> getEmployeeById(String id) {
        EmployeeDTO employeeDTO = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employeeDTO, HttpStatus.OK);

    }

    /**
     * Get the highest salary of employees
     * @return Integer of the highest salary
     *
     * TODO: Caching can be added in future to store the highest salary as it is not expected to change frequently
     */
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return new ResponseEntity<>(employeeService.getHighestSalaryOfEmployees(), HttpStatus.OK);
    }

    /**
     * Get the top ten highest earning employee names
     * @return List of employee names
     *
     * TODO: Caching can be added in future to store the highest salary as it is not expected to change frequently
     */
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return new ResponseEntity<>(employeeService.getTopTenHighestEarningEmployeeNames(), HttpStatus.OK);
    }

    /**
     * Create a new employee
     * @param newEmployee Request DTO to create new employee
     * @return EmployeeDTO of the created employee
     */
    @Override
    public ResponseEntity<EmployeeDTO> createEmployee(CreateEmployeeRequestDTO newEmployee) {
        return new ResponseEntity<>(employeeService.createEmployee(newEmployee), HttpStatus.CREATED);
    }

    /**
     * Delete employee by id
     * @param id Employee id to delete
     * @return Name string of employee deleted
     */
    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return new ResponseEntity<>(employeeService.deleteEmployeeById(id), HttpStatus.OK);
    }
}
