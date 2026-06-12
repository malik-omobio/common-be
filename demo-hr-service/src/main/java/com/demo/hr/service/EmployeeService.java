package com.demo.hr.service;

import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.demo.hr.dto.employee.*;

import java.util.UUID;

public interface EmployeeService {
    ResponseEmployeeDTO create(CreateEmployeeDTO dto);
    ResponseEmployeeDTO update(UUID id, UpdateEmployeeDTO dto);
    ResponseEmployeeDTO findById(UUID id);
    ApiPaginatedResponse<ResponseEmployeeDTO> findAll(FilterEmployeeDTO filter);
    void delete(UUID id);
}
