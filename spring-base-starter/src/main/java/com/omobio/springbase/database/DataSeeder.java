package com.omobio.springbase.database;

/**
 * Application-supplied seeder that runs (in bean order) after the core
 * permission/role/admin-user seeding when the {@code seed} profile is active.
 */
public interface DataSeeder {
    void run();
}
