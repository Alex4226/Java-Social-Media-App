package repository;

import domain.User;
import domain.validators.Validator;

import java.util.Optional;

/**
 * Class for user repository in database. Implements repository
 */
public class UserDbRepository implements Repository<Long, User> {
    private final Validator<User> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructor for user db repository
     * @param validator validator of users
     * @param url The JDBC URL used to connect to the database.
     * @param username The username credential for authenticating with the db
     * @param password The password credential for authentication with the db
     */
    public UserDbRepository(Validator<User> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> find(Long id) {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }

    @Override
    public Iterable<User> getAll() {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }

    @Override
    public Optional<User> add(User entity) {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }

    @Override
    public Optional<User> delete(Long id) {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }

    @Override
    public Optional<User> update(User entity) {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }
}
