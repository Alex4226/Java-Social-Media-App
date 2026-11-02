package domain.validators;

import java.util.Objects;

/**
 * Functional interface used to validate entities within the domain model.
 *
 * @param <T> the type of entity to be validated
 */
@FunctionalInterface
public interface Validator<T> {

    /**
     * Validates the specified entity against defined business and consistency rules.
     *
     * @param entity the entity to validate
     * @throws ValidationException if the entity violates any validation rule
     */
    void validate(T entity) throws ValidationException;

    /**
     * Returns a composed {@code Validator} that performs, in sequence, this operation
     * followed by the {@code other} operation.
     *
     * @param other the other validator to be executed after this one
     * @return a composed {@code Validator} executing both validations sequentially
     * @throws NullPointerException if {@code other} is null
     */
    default Validator<T> andThen(Validator<T> other) {
        Objects.requireNonNull(other, "The following validator must not be null");
        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}