package repository;

import domain.User;
import domain.validators.Validator;

import java.util.List;

/**
 * File-backed repository implementation for {@link User} entities.
 */
public class UserFileRepository extends AbstractFileRepository<Long, User> {

    /**
     * Constructs a new UserFileRepository with the specified validator and file path.
     *
     * @param validator the validator used to enforce business constraints on users
     * @param filename  the path to the file storing serialized user records
     */
    public UserFileRepository(Validator<User> validator, String filename) {
        super(validator, filename);
    }

    /**
     * Deserializes a user entity from a list of string tokens extracted from a file record.
     *
     * @param attributes the ordered list of attributes parsed from a single line
     * @return the constructed {@link User} instance
     * @throws IllegalArgumentException if the list contains fewer than 4 attributes or the ID is malformed
     */
    @Override
    public User extractEntity(List<String> attributes) {
        if (attributes.size() < 4) {
            throw new IllegalArgumentException("Invalid line format: expected 4 fields");
        }

        Long id = Long.parseLong(attributes.get(0).trim());
        String firstName = attributes.get(1).trim();
        String lastName = attributes.get(2).trim();
        String email = attributes.get(3).trim();

        return new User(id, firstName, lastName, email);
    }

    /**
     * Serializes a {@link User} entity into a semicolon-delimited string line.
     *
     * @param entity the user to be serialized
     * @return the formatted string representation ready for file storage
     */
    @Override
    protected String createEntityAsString(User entity) {
        Long id = entity.getId();
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String email = entity.getEmail();

        return id + ";" + firstName + ";" + lastName + ";" + email;
    }
}