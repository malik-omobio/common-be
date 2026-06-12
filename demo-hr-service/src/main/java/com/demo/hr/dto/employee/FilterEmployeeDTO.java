package com.demo.hr.dto.employee;

import com.demo.hr.common.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterEmployeeDTO {
    private String search;
    private String department;
    private EmployeeStatus status;
    private int page;
    private int perPage;
}
