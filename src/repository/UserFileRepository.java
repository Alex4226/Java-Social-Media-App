package repository;

import domain.User;
import domain.validators.Validator;

import java.util.List;

/**
 * Class for user file repository that extends abstract file repository
 */
public class UserFileRepository extends AbstractFileRepository<Long, User>{

    /**
     * Constructor for user file repository class
     * @param validator used validator for validation of users
     * @param filename the filename for the file that saves the users
     */
    public UserFileRepository(Validator<User> validator, String filename) {
        super(validator, filename);
    }

    @Override
    public User extractEntity(List<String> attributes) {
        if (attributes.size() < 4) {
            throw new IllegalArgumentException("Invalid line format: expected 4 fields");
        }

        Long id = Long.parseLong(attributes.get(0).trim());
        String firstName = attributes.get(1).trim();
        String lastName = attributes.get(2).trim();
        String email = attributes.get(3).trim();

        User user = new User(id, firstName, lastName, email);
        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        Long id = entity.getId();
        String firstName = entity.getFirstName();
        String lastName = entity.getLastName();
        String email = entity.getEmail();

        return id + ";" + firstName + ";" + lastName + ";" + email;
    }
}
