package com.test.crud.service.impl;

import com.test.crud.common.exception.CustomException;
import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.test.crud.dto.employee.*;
import com.test.crud.mapper.EmployeeMapper;
import com.test.crud.model.Employee;
import com.test.crud.repository.EmployeeRepository;
import com.test.crud.service.EmployeeService;
import com.test.crud.specification.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public ResponseEmployeeDTO create(CreateEmployeeDTO dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException("Employee email already exists", HttpStatus.BAD_REQUEST);
        }
        Employee saved = employeeRepository.save(employeeMapper.toEntity(dto));
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ResponseEmployeeDTO update(UUID id, UpdateEmployeeDTO dto) {
        Employee employee = findEntity(id);
        if (dto.getEmail() != null && employeeRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new CustomException("Employee email already exists", HttpStatus.BAD_REQUEST);
        }
        employeeMapper.updateEntity(employee, dto);
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEmployeeDTO findById(UUID id) {
        return employeeMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPaginatedResponse<ResponseEmployeeDTO> findAll(FilterEmployeeDTO filter) {
        PageRequest pageable = PageRequest.of(
                filter.getPage(),
                filter.getPerPage(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Employee> page = employeeRepository.findAll(
                EmployeeSpecification.withFilters(filter), pageable);

        return new ApiPaginatedResponse<>(
                page.getContent().stream().map(employeeMapper::toResponse).toList(),
                new ApiPaginatedResponse.Pagination(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getTotalPages(),
                        page.hasNext()
                )
        );
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new CustomException("Employee not found", HttpStatus.NOT_FOUND);
        }
        employeeRepository.deleteById(id);
    }

    private Employee findEntity(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));
    }
}
