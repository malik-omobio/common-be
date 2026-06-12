package __BASE_PACKAGE__.dto.employee;

import __BASE_PACKAGE__.common.enums.EmployeeStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ResponseEmployeeDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String jobTitle;
    private BigDecimal salary;
    private EmployeeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
