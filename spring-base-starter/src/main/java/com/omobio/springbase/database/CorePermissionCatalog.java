package com.omobio.springbase.database;

import com.omobio.springbase.common.constants.CorePermissions;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/** Permissions for the built-in user/role management endpoints. */
@Component
public class CorePermissionCatalog implements PermissionCatalog {

    @Override
    public Map<String, Map<String, String>> categories() {
        Map<String, Map<String, String>> categories = new LinkedHashMap<>();
        categories.put("User", Map.of(
                CorePermissions.VIEW_USER, "View User",
                CorePermissions.CREATE_USER, "Create User",
                CorePermissions.UPDATE_USER, "Update User",
                CorePermissions.ASSIGN_USER_PERMISSION, "Assign User Permission"
        ));
        categories.put("Role", Map.of(
                CorePermissions.VIEW_ROLE, "View Role",
                CorePermissions.CREATE_ROLE, "Create Role",
                CorePermissions.UPDATE_ROLE, "Update Role",
                CorePermissions.DELETE_ROLE, "Delete Role",
                CorePermissions.ASSIGN_ROLE_PERMISSION, "Assign Role Permission",
                CorePermissions.VIEW_ROLE_DROPDOWN, "View Role Dropdown"
        ));
        return categories;
    }
}
