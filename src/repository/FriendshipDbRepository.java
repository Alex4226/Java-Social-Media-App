package repository;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;

/**
 * Class for friendship repository in database. Implements repository
 */
public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructor for friendship db repository
     * @param validator validator of friendships
     * @param url The JDBC URL used to connect to the database.
     * @param username The username credential for authenticating with the db
     * @param password The password credential for authentication with the db
     */
    public FriendshipDbRepository(Validator<Friendship> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Friendship find(Tuple<Long, Long> id) {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }

    @Override
    public Iterable<Friendship> getAll() {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }

    @Override
    public Friendship add(Friendship entity) {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }

    @Override
    public Friendship delete(Tuple<Long, Long> id) {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }

    @Override
    public Friendship update(Friendship entity) {
        throw new UnsupportedOperationException("Database repository not implemented yet.");
    }
}
