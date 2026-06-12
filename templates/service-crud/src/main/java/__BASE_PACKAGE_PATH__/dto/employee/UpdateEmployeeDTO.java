package __BASE_PACKAGE__.dto.employee;

import __BASE_PACKAGE__.common.enums.EmployeeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateEmployeeDTO {
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String department;
    private String jobTitle;
    @DecimalMin("0.0")
    private BigDecimal salary;
    private EmployeeStatus status;
}
