package __BASE_PACKAGE__.mapper;

import __BASE_PACKAGE__.common.enums.EmployeeStatus;
import __BASE_PACKAGE__.dto.employee.CreateEmployeeDTO;
import __BASE_PACKAGE__.dto.employee.ResponseEmployeeDTO;
import __BASE_PACKAGE__.dto.employee.UpdateEmployeeDTO;
import __BASE_PACKAGE__.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(CreateEmployeeDTO dto) {
        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .jobTitle(dto.getJobTitle())
                .salary(dto.getSalary())
                .status(dto.getStatus() != null ? dto.getStatus() : EmployeeStatus.ACTIVE)
                .build();
    }

    public void updateEntity(Employee employee, UpdateEmployeeDTO dto) {
        if (dto.getFirstName() != null) employee.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) employee.setLastName(dto.getLastName());
        if (dto.getEmail() != null) employee.setEmail(dto.getEmail());
        if (dto.getDepartment() != null) employee.setDepartment(dto.getDepartment());
        if (dto.getJobTitle() != null) employee.setJobTitle(dto.getJobTitle());
        if (dto.getSalary() != null) employee.setSalary(dto.getSalary());
        if (dto.getStatus() != null) employee.setStatus(dto.getStatus());
    }

    public ResponseEmployeeDTO toResponse(Employee employee) {
        return ResponseEmployeeDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .jobTitle(employee.getJobTitle())
                .salary(employee.getSalary())
                .status(employee.getStatus())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
