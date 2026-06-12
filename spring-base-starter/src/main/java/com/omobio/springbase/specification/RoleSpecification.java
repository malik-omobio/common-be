package com.omobio.springbase.specification;

import com.omobio.springbase.dto.role.FilterRoleDTO;
import com.omobio.springbase.model.Role;
import org.springframework.data.jpa.domain.Specification;

public final class RoleSpecification {

    private RoleSpecification() {}

    public static Specification<Role> withFilters(FilterRoleDTO filter) {
        return nameContains(filter.getName());
    }

    private static Specification<Role> nameContains(String name) {
        return (root, query, cb) ->
                name == null || name.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}
