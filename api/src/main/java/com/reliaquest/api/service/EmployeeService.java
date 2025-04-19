package com.reliaquest.api.service;

import com.reliaquest.api.client.ServerApiClient;
import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.InvalidRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class EmployeeService {
    private final static Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private final ServerApiClient serverApiClient;

    public List<EmployeeDTO> getAllEmployee() {
        if(log.isDebugEnabled()) {
            log.debug("Fetching all employees from server");
        }
        List<EmployeeDTO> allEmployees = serverApiClient.getAllEmployee();
        if(log.isDebugEnabled()) {
            log.debug("Fetched {} employees from server", allEmployees.size());
        }
        return allEmployees;
    }

    public List<EmployeeDTO> getEmployeesByNameSearch(String searchString) {
        if (searchString == null || searchString.isBlank()) {
            throw new InvalidRequestException("Search string must not be null or empty");
        }

        if(log.isDebugEnabled()) {
            log.debug("Searching for employees with name containing: {}", searchString);
        }

        /*
        * First fetch all the employees and then filter the employees whose name contains the search string.
        */
        List<EmployeeDTO> allEmployees = serverApiClient.getAllEmployee();
        List<EmployeeDTO> searchedEmployees = allEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .sorted((e1, e2) -> {
                    String name1 = e1.getName().toLowerCase();
                    String name2 = e2.getName().toLowerCase();

                    boolean starts1 = name1.startsWith(searchString.toLowerCase());
                    boolean starts2 = name2.startsWith(searchString.toLowerCase());

                    /*
                    * Sort the data in such a way that the employees whose names start with the search string
                    * will be at the top of the list.
                    * And the rest of the employees whose name contains the search string will be sorted alphabetically.
                    */
                    if (starts1 && !starts2) {
                        return -1;
                    }

                    if (!starts1 && starts2) {
                        return 1;
                    }

                    //If both names do not start with searchString then sort them alphabetically
                    return name1.compareTo(name2);
                }).toList();

        if (log.isDebugEnabled()) {
            log.debug("Found {} employees with name containing: {}", searchedEmployees.size(), searchString);
        }
        return searchedEmployees;
    }

    public EmployeeDTO getEmployeeById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidRequestException("Employee ID must not be null or empty");
        }
        if(log.isDebugEnabled()) {
            log.debug("Fetching employee with id: {}", id);
        }
        return serverApiClient.getEmployeeById(id);
    }

    public Integer getHighestSalaryOfEmployees() {
        if(log.isDebugEnabled()) {
            log.debug("Fetching highest salary of employees");
        }

        List<EmployeeDTO> allEmployees = serverApiClient.getAllEmployee();
        Integer highestSalary = allEmployees.stream()
                .mapToInt(EmployeeDTO::getSalary)
                .max()
                .orElse(0);

        log.info("Highest salary among all employees is : {}", highestSalary);
        return highestSalary;
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        if(log.isDebugEnabled()) {
            log.debug("Fetching top 10 highest earning employee names");
        }
        List<EmployeeDTO> allEmployees = serverApiClient.getAllEmployee();
        List<String> top10HighestEarningEmployeeNames =  allEmployees.stream()
                .sorted((e1, e2) -> Integer.compare(e2.getSalary(), e1.getSalary()))
                .limit(10)
                .map(EmployeeDTO::getName)
                .toList();

        if(log.isDebugEnabled()) {
            log.debug("Top 10 highest earning employee names: {}", top10HighestEarningEmployeeNames);
        }
        return top10HighestEarningEmployeeNames;
    }

    public EmployeeDTO createEmployee(@Valid CreateEmployeeRequestDTO newEmployee) {
        if(log.isDebugEnabled()) {
            log.debug("Creating new employee with details: {}", newEmployee);
        }
        SingleEmployeeResponseDTO singleEmployeeResponseDTO = serverApiClient.createEmployee(newEmployee);
        if(singleEmployeeResponseDTO.getData() == null) {
            log.error("Failed to create employee: {}", singleEmployeeResponseDTO);
            throw new RuntimeException("Failed to create employee");
        }

        return singleEmployeeResponseDTO.getData();
    }

    public String deleteEmployeeById(String id) {
        if(id == null || id.isEmpty()) {
            throw new InvalidRequestException("Employee ID must not be null or empty");
        }

        if(log.isDebugEnabled()) {
            log.debug("Deleting employee with id: {}", id);
        }

        EmployeeDTO employeeToDelete = serverApiClient.getEmployeeById(id);

        if(log.isDebugEnabled()) {
            log.debug("Employee to delete: {}", employeeToDelete);
        }

        DeleteEmployeeResponseDTO deleteEmployeeResponseDTO = serverApiClient.deleteEmployeeByName(
                DeleteEmployeeRequestDTO
                        .builder()
                        .name(employeeToDelete.getName()).build());

        if(deleteEmployeeResponseDTO.getData()) {
            return employeeToDelete.getName();
        } else {
            log.error("Failed to delete employee with id: {}", id);
            throw new RuntimeException("Failed to delete employee with id: " + id);
        }
    }
}
