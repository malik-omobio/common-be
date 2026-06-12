package com.demo.hr.database;

import com.demo.hr.common.constants.EmployeePermissions;
import com.omobio.springbase.database.PermissionCatalog;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Contributes the Employee permissions to the shared permission seeder. */
@Component
public class EmployeePermissionCatalog implements PermissionCatalog {

    @Override
    public Map<String, Map<String, String>> categories() {
        return Map.of("Employee", Map.of(
                EmployeePermissions.VIEW_EMPLOYEE, "View Employee",
                EmployeePermissions.CREATE_EMPLOYEE, "Create Employee",
                EmployeePermissions.UPDATE_EMPLOYEE, "Update Employee",
                EmployeePermissions.DELETE_EMPLOYEE, "Delete Employee"
        ));
    }
}
