package __BASE_PACKAGE__.specification;

import __BASE_PACKAGE__.common.enums.EmployeeStatus;
import __BASE_PACKAGE__.dto.employee.FilterEmployeeDTO;
import __BASE_PACKAGE__.model.Employee;
import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecification {

    private EmployeeSpecification() {}

    public static Specification<Employee> withFilters(FilterEmployeeDTO filter) {
        return Specification
                .where(search(filter.getSearch()))
                .and(departmentEquals(filter.getDepartment()))
                .and(statusEquals(filter.getStatus()));
    }

    private static Specification<Employee> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return cb.conjunction();
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }

    private static Specification<Employee> departmentEquals(String department) {
        return (root, query, cb) ->
                department == null || department.isBlank()
                        ? cb.conjunction()
                        : cb.equal(cb.lower(root.get("department")), department.toLowerCase());
    }

    private static Specification<Employee> statusEquals(EmployeeStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }
}
