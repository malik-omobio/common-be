package com.test.crud.dto.employee;

import com.test.crud.common.enums.EmployeeStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateEmployeeDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String department;
    @NotBlank
    private String jobTitle;
    @NotNull @DecimalMin("0.0")
    private BigDecimal salary;
    private EmployeeStatus status;
}
