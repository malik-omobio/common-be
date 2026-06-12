package __BASE_PACKAGE__.dto.employee;

import __BASE_PACKAGE__.common.enums.EmployeeStatus;
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
