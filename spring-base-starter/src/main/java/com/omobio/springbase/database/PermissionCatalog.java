package com.omobio.springbase.database;

import java.util.Map;

/**
 * Contributes permissions to the seeder. The starter registers the core
 * user/role catalog; applications add their own beans for domain permissions:
 *
 * <pre>
 * &#64;Component
 * class EmployeePermissionCatalog implements PermissionCatalog {
 *     public Map&lt;String, Map&lt;String, String&gt;&gt; categories() {
 *         return Map.of("Employee", Map.of("VIEW_EMPLOYEE", "View Employee"));
 *     }
 * }
 * </pre>
 */
public interface PermissionCatalog {

    /** category name -> (permission key -> display name) */
    Map<String, Map<String, String>> categories();
}
