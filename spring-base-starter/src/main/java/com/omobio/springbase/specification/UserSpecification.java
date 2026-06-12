package com.omobio.springbase.specification;

import com.omobio.springbase.dto.user.FilterUserDTO;
import com.omobio.springbase.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class UserSpecification {

    private UserSpecification() {}

    public static Specification<User> withFilters(FilterUserDTO filter) {
        return Specification
                .where(emailContains(filter.getEmail()))
                .and(roleEquals(filter.getRoleId()))
                .and(statusEquals(filter.getStatus()));
    }

    private static Specification<User> emailContains(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<User> roleEquals(UUID roleId) {
        return (root, query, cb) ->
                roleId == null ? cb.conjunction() : cb.equal(root.get("role").get("id"), roleId);
    }

    private static Specification<User> statusEquals(com.omobio.springbase.common.enums.UserStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }
}
