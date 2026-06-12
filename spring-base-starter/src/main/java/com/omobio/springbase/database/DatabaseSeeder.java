package com.omobio.springbase.database;

import com.omobio.springbase.database.seeder.PermissionSeeder;
import com.omobio.springbase.database.seeder.RoleSeeder;
import com.omobio.springbase.database.seeder.UserSeeder;
import com.omobio.springbase.model.Role;
import com.omobio.springbase.security.PermissionLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final Environment environment;
    private final PermissionSeeder permissionSeeder;
    private final RoleSeeder roleSeeder;
    private final UserSeeder userSeeder;
    private final List<DataSeeder> dataSeeders;
    private final PermissionLoaderService permissionLoaderService;

    @Override
    public void run(String... args) {
        if (!environment.acceptsProfiles("seed")) {
            return;
        }
        permissionSeeder.run();
        Role adminRole = roleSeeder.seedAdminRole();
        userSeeder.seedAdmin(adminRole);
        dataSeeders.forEach(DataSeeder::run);
        permissionLoaderService.reloadAllActiveUserPermissions();
    }
}
