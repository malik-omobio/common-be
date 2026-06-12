package com.test.crud.database;

import com.test.crud.common.constants.EmployeePermissions;
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
