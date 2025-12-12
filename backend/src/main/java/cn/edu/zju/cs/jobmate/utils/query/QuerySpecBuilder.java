package cn.edu.zju.cs.jobmate.utils.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import cn.edu.zju.cs.jobmate.exceptions.BusinessException;
import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * JPA specification cb used in keyword and filter query.
 */
public class QuerySpecBuilder {

    /**
     * Build a {@link Specification} for JPA query.
     * @param <T> the entity type
     * @param keyword the keyword to search for
     * @param fields the fields for keyword to search in
     * @param filters the filters to apply
     * @return the JPA specification
     * @throws BusinessException if field is not of type String
     */
    @SuppressWarnings("unchecked")
    public static <T> Specification<T> build(
        String keyword,
        List<String> fields,
        Map<String, Enum<?>> filters
    ) throws BusinessException {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword search predicates.
            if (keyword != null && !keyword.isBlank()) {
                // Parse keyword into tokens by whitespace.
                List<String> tokens = List.of(keyword.trim().split("\\s+"));
                List<Predicate> tokenPredicates = new ArrayList<>();
                for (String token : tokens) {
                    String pattern = "%" + token.toLowerCase() + "%";
                    List<Predicate> fieldPredicates = new ArrayList<>();
                    for (String field : fields) {
                        Path<T> path = getPath(root, field);
                        if (path.getJavaType() != String.class) {
                            throw new BusinessException(ErrorCode.INVALID_PARAMETER);
                        }
                        fieldPredicates.add(cb.like(cb.lower((Path<String>) path), pattern));
                    }
                    // Each token must match at least one field (OR).
                    tokenPredicates.add(cb.or(fieldPredicates.toArray(new Predicate[0])));
                }
                // All tokens must be matched (AND).
                predicates.add(cb.and(tokenPredicates.toArray(new Predicate[0])));
            }

            // Filters predicates.
            if (filters != null && !filters.isEmpty()) {
                List<Predicate> filterPredicates = new ArrayList<>();
                // Apply each filter.
                filters.forEach((field, filter) -> {
                    if (filter != null) {
                        filterPredicates.add(cb.equal(getPath(root, field), filter));
                    }
                });
                // All filters must match (AND).
                predicates.add(cb.and(filterPredicates.toArray(new Predicate[0])));
            }

            // Combine all predicates (AND).
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> Path<T> getPath(Path<T> root, String field) {
        String[] parts = field.split("\\.");
        Path<T> path = root;
        // Navigate through nested fields.
        for (String part : parts) {
            path = path.get(part);
        }
        return path;
    }
}
