package com.reliaquest.api.client;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.ExternalServiceException;
import com.reliaquest.api.exception.InvalidRequestException;
import com.reliaquest.api.util.HttpHeaderUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Implementation of the ServerApiClient interface for interacting with the Server.
 * This class handles HTTP requests and responses, including error handling.
 */
@Service
@RequiredArgsConstructor
public class ServerApiClientImpl implements ServerApiClient {

    private static final Logger log = LoggerFactory.getLogger(ServerApiClientImpl.class);
    private final RestTemplate restTemplate;
    private final static String BASE_ENDPOINT = "/employee";

    /**
     * Get all employees
     * @return List of EmployeeDTO
     */
    @Override
    public List<EmployeeDTO> getAllEmployee() {
        try {
            EmployeeResponseDTO response = restTemplate.getForObject(BASE_ENDPOINT, EmployeeResponseDTO.class);

            if (response == null || response.getData() == null) {
                throw new ExternalServiceException("Received null or empty response from Employee Service");
            }
            return response.getData();
        } catch (RestClientException ex) {
            log.error("Failed to fetch employee data from server: {}", ex.getMessage(), ex);
            throw new ExternalServiceException("Failed to connect to Employee Service", ex);
        } catch (Exception ex) {
            log.error("Unexpected error while fetching employees: {}", ex.getMessage(), ex);
            throw new RuntimeException("Unexpected error occurred while fetching employees", ex);
        }
    }


    /**
     * Get employee by id
     * @param id Employee id
     * @return EmployeeDTO of the employee searched by id
     */
    @Override
    public EmployeeDTO getEmployeeById(String id) {
        String endpoint = BASE_ENDPOINT + "/" + id;

        try {
            SingleEmployeeResponseDTO response =
                    restTemplate.getForObject(endpoint, SingleEmployeeResponseDTO.class);

            if(log.isDebugEnabled()) {
                log.debug("Response from getEmployeeById: {}", response);
            }

            if (response == null || response.getData() == null) {
                log.error("Employee not found with id {}", id);
                throw new EmployeeNotFoundException("Employee not found with id: " + id);
            }

            return response.getData();

        } catch (HttpClientErrorException ex) {
            // If server responds with 404
            log.error("Employee not found with id {}: {}", id, ex.getMessage(), ex);
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        } catch (RestClientException ex) {
            log.error("Error fetching employee with id {}: {}", id, ex.getMessage(), ex);
            throw new RuntimeException("Failed to fetch employee with id: " + id, ex);
        }
    }


    /**
     * Create new employee
     * @param createEmployeeRequestDTO Request DTO to create new employee
     * @return SingleEmployeeResponseDTO of the created employee
     */
    @Override
    public SingleEmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO createEmployeeRequestDTO) {
        try {
            HttpEntity<CreateEmployeeRequestDTO> requestEntity =
                    new HttpEntity<>(createEmployeeRequestDTO, HttpHeaderUtil.getDefaultHeaders());

            ResponseEntity<SingleEmployeeResponseDTO> response = restTemplate.postForEntity(
                    BASE_ENDPOINT,
                    requestEntity,
                    SingleEmployeeResponseDTO.class
            );

            if(log.isDebugEnabled()) {
                log.debug("Response from createEmployee: {}", response);
            }

            return response.getBody();
        } catch (HttpClientErrorException e) {
            // Handle 4xx errors
            log.error("Client error while adding new employee: {}", e.getMessage(), e);
            throw new InvalidRequestException("Failed to create employee: " + e.getStatusCode().value());
        } catch (HttpServerErrorException e) {
            // Handle 5xx errors
            log.error("Server error while adding new employee: {}", e.getMessage(), e);
            throw new ExternalServiceException("Failed to create employee due to server error: " + e.getStatusCode().value());
        } catch (RestClientException e) {
            log.error("Unexpected error while adding new employee", e);
            throw new ExternalServiceException("Unable to create employee at the moment. Please try again later.");
        }
    }


    /**
     * Delete employee by id
     * @param deleteEmployeeRequestDTO Request DTO to delete employee
     * @return DeleteEmployeeResponseDTO of the deleted employee
     */
    @Override
    public DeleteEmployeeResponseDTO deleteEmployeeByName(DeleteEmployeeRequestDTO deleteEmployeeRequestDTO) {
        try {
            HttpEntity<DeleteEmployeeRequestDTO> requestEntity = new HttpEntity<>(
                    deleteEmployeeRequestDTO, HttpHeaderUtil.getDefaultHeaders());

            ResponseEntity<DeleteEmployeeResponseDTO> response = restTemplate.exchange(
                    BASE_ENDPOINT,
                    HttpMethod.DELETE,
                    requestEntity,
                    DeleteEmployeeResponseDTO.class,
                    deleteEmployeeRequestDTO.getName()
            );

            if(log.isDebugEnabled()) {
                log.debug("Response from deleteEmployeeByName: {}", response);
            }

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Employee with name '{}' not found.", deleteEmployeeRequestDTO.getName(), e);
            throw new EmployeeNotFoundException("Employee with name '" + deleteEmployeeRequestDTO.getName() + "' not found.");
        } catch (HttpClientErrorException e) {
            log.error("Client error during deleteEmployeeByName: {}", e.getResponseBodyAsString(), e);
            throw new InvalidRequestException("Invalid request: " + e.getStatusCode());
        } catch (HttpServerErrorException e) {
            log.error("Server error during deleteEmployeeByName: {}", e.getResponseBodyAsString(), e);
            throw new ExternalServiceException("Failed to delete employee due to server error.");
        } catch (RestClientException e) {
            log.error("Unexpected error during deleteEmployeeByName", e);
            throw new ExternalServiceException("Unexpected error occurred while deleting employee.");
        }
    }
}
