package com.omobio.springbase.common.constants;

/** Permission keys for the built-in user/role/permission management endpoints. */
public final class CorePermissions {

    private CorePermissions() {}

    public static final String VIEW_USER = "VIEW_USER";
    public static final String CREATE_USER = "CREATE_USER";
    public static final String UPDATE_USER = "UPDATE_USER";
    public static final String ASSIGN_USER_PERMISSION = "ASSIGN_USER_PERMISSION";

    public static final String VIEW_ROLE = "VIEW_ROLE";
    public static final String CREATE_ROLE = "CREATE_ROLE";
    public static final String ASSIGN_ROLE_PERMISSION = "ASSIGN_ROLE_PERMISSION";
    public static final String VIEW_ROLE_DROPDOWN = "VIEW_ROLE_DROPDOWN";
}
