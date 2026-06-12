package com.demo.hr.database.seeder;

import com.demo.hr.common.enums.EmployeeStatus;
import com.omobio.springbase.database.DataSeeder;
import com.demo.hr.model.Employee;
import com.demo.hr.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class EmployeeSeeder implements DataSeeder {

    private final EmployeeRepository employeeRepository;

    @Override
    public void run() {
        if (employeeRepository.count() > 0) {
            return;
        }
        employeeRepository.save(Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@company.com")
                .department("Engineering")
                .jobTitle("Software Engineer")
                .salary(new BigDecimal("75000.00"))
                .status(EmployeeStatus.ACTIVE)
                .build());
        employeeRepository.save(Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@company.com")
                .department("HR")
                .jobTitle("HR Manager")
                .salary(new BigDecimal("68000.00"))
                .status(EmployeeStatus.ACTIVE)
                .build());
    }
}
