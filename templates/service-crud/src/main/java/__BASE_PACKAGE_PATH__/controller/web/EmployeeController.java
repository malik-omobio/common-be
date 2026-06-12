package __BASE_PACKAGE__.controller.web;

import __BASE_PACKAGE__.common.enums.EmployeeStatus;
import com.omobio.springbase.common.response.ApiPaginatedResponse;
import com.omobio.springbase.common.response.ApiResponse;
import __BASE_PACKAGE__.dto.employee.FilterEmployeeDTO;
import __BASE_PACKAGE__.dto.employee.ResponseEmployeeDTO;
import __BASE_PACKAGE__.service.EmployeeService;
import com.omobio.springbase.util.constants.Prefixes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController("web-employee")
@RequestMapping(Prefixes.WEB_PATH + "/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public ApiResponse<ResponseEmployeeDTO> findById(@PathVariable UUID id) {
        return new ApiResponse<>(employeeService.findById(id), "Employee fetched successfully");
    }

    @GetMapping
    public ApiPaginatedResponse<ResponseEmployeeDTO> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage) {
        FilterEmployeeDTO filter = new FilterEmployeeDTO(search, department, status, page, perPage);
        return employeeService.findAll(filter);
    }
}
