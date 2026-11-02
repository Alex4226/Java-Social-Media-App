package domain.validators;

import domain.User;

/**
 * Concrete validator for User entities, implementing the Strategy pattern[cite: 1, 3].
 */
public class UserValidator implements Validator<User> {

    /**
     * Validates a given User entity against core domain rules[cite: 1, 3].
     *
     * @param entity the user entity to validate[cite: 1, 3]
     * @throws ValidationException if the entity is null, has blank names, or an invalid email format[cite: 3]
     */
    @Override
    public void validate(User entity) {
        if (entity == null) {
            throw new ValidationException("User cannot be null!");
        }

        if (entity.getFirstName() == null || entity.getFirstName().trim().isEmpty()) {
            throw new ValidationException("User needs to have a first name!");
        }

        if (entity.getLastName() == null || entity.getLastName().trim().isEmpty()) {
            throw new ValidationException("User needs to have a last name!");
        }

        if (entity.getEmail() == null || !entity.getEmail().contains("@")) {
            throw new ValidationException("User has invalid email (needs to have an @)");
        }
    }
}