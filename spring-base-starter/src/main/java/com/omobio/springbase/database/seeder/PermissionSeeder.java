package com.omobio.springbase.database.seeder;

import com.omobio.springbase.database.PermissionCatalog;
import com.omobio.springbase.model.PermissionEntity;
import com.omobio.springbase.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermissionSeeder {

    private final PermissionRepository permissionRepository;
    private final List<PermissionCatalog> catalogs;

    public void run() {
        for (PermissionCatalog catalog : catalogs) {
            for (Map.Entry<String, Map<String, String>> category : catalog.categories().entrySet()) {
                String categoryName = category.getKey();
                for (Map.Entry<String, String> entry : category.getValue().entrySet()) {
                    String permissionKey = entry.getKey();
                    String displayName = entry.getValue();
                    permissionRepository.findByName(permissionKey).ifPresentOrElse(
                            existing -> {
                                existing.setKey(permissionKey);
                                existing.setDisplayName(displayName);
                                existing.setCategory(categoryName);
                                permissionRepository.save(existing);
                            },
                            () -> permissionRepository.save(PermissionEntity.builder()
                                    .name(permissionKey)
                                    .key(permissionKey)
                                    .displayName(displayName)
                                    .category(categoryName)
                                    .build())
                    );
                }
            }
        }
    }
}
