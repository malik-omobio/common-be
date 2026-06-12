package com.test.crud.controller.admin;

import com.test.crud.common.enums.EmployeeStatus;
import com.test.crud.common.constants.EmployeePermissions;
import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.omobio.springbase.common.response.ApiResponse;
import com.test.crud.dto.employee.*;
import com.test.crud.service.EmployeeService;
import com.omobio.springbase.util.abst.Guard;
import com.omobio.springbase.util.constants.Prefixes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("admin-employee")
@RequestMapping(Prefixes.ADMIN_PATH + "/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @Guard({EmployeePermissions.CREATE_EMPLOYEE})
    @PostMapping
    public ApiResponse<ResponseEmployeeDTO> create(@Valid @RequestBody CreateEmployeeDTO dto) {
        return new ApiResponse<>(employeeService.create(dto), "Employee created successfully");
    }

    @Guard({EmployeePermissions.UPDATE_EMPLOYEE})
    @PutMapping("/{id}")
    public ApiResponse<ResponseEmployeeDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeDTO dto) {
        return new ApiResponse<>(employeeService.update(id, dto), "Employee updated successfully");
    }

    @Guard({EmployeePermissions.VIEW_EMPLOYEE})
    @GetMapping("/{id}")
    public ApiResponse<ResponseEmployeeDTO> findById(@PathVariable UUID id) {
        return new ApiResponse<>(employeeService.findById(id), "Employee fetched successfully");
    }

    @Guard({EmployeePermissions.VIEW_EMPLOYEE})
    @GetMapping
    public ApiPaginatedResponse<ResponseEmployeeDTO> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage) {
        return employeeService.findAll(new FilterEmployeeDTO(search, department, status, page, perPage));
    }

    @Guard({EmployeePermissions.DELETE_EMPLOYEE})
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        employeeService.delete(id);
        return new ApiResponse<>("Employee deleted successfully");
    }
}
