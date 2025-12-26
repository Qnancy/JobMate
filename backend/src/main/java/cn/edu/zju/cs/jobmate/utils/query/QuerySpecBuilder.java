package cn.edu.zju.cs.jobmate.utils.query;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * JPA {@link Specification} builder used in keyword and filter query.
 */
public class QuerySpecBuilder {

    /**
     * Fields to search the keyword in.
     */
    public static class Fields {
        private List<String> fields; // List of field names.
        public Fields(String... fields) { this.fields = List.of(fields); }
        public static Fields of(String... fields) { return new Fields(fields); }
        public List<String> getFields() { return fields; }
    }

    /**
     * Filter to apply in the query.
     */
    public static class Filter {
        private String field; // Filter field name.
        private Enum<?> value; // Filter value.
        public Filter(String field, Enum<?> value) {
            this.field = field;
            this.value = value;
        }
        public static Filter of(String field, Enum<?> value) {
            return new Filter(field, value);
        }
        public String getField() { return field; }
        public Enum<?> getFilter() { return value; }
    }
    /**
     * Build a {@link Specification} for JPA query.
     * @param <T> the entity type
     * @param keyword the keyword to search for
     * @param fields the {@link Fields} to search the keyword in
     * @param filters the {@link Filter}s to apply
     * @return the JPA specification
     */
    @SuppressWarnings("unchecked")
    public static <T> Specification<T> build(
        String keyword,
        Fields fields,
        Filter... filters
    ) {
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
                    for (String field : fields.getFields()) {
                        Path<T> path = getPath(root, field);
                        fieldPredicates.add(cb.like(cb.lower((Path<String>) path), pattern));
                    }
                    // Each token must match at least one field (OR).
                    tokenPredicates.add(cb.or(fieldPredicates.toArray(new Predicate[0])));
                }
                // All tokens must be matched (AND).
                predicates.add(cb.and(tokenPredicates.toArray(new Predicate[0])));
            }

            // Filters predicates.
            if (filters.length > 0) {
                List<Predicate> filterPredicates = new ArrayList<>();
                // Apply each filter.
                for (Filter filter : filters) {
                    if (filter.getFilter() != null) {
                        filterPredicates.add(cb.equal(
                            getPath(root, filter.getField()),
                            filter.getFilter()
                        ));
                    }
                }
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
