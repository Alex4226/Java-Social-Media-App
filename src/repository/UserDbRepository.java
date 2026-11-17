package repository;

import domain.User;
import domain.validators.Validator;
import repository.exceptions.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    private User extractUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String first_name = rs.getString("first_name");
        String last_name = rs.getString("last_name");
        String email = rs.getString("email");

        User u = new User(id, first_name, last_name, email);

        return u;
    }

    @Override
    public Optional<User> find(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null!");
        }

        String query = "SELECT id, first_name, last_name, email FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractUser(rs));
                }
                else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error while finding user", e);
        }
    }

    @Override
    public Iterable<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM users";
        try (Connection conn = DriverManager.getConnection(url, username, password); PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    users.add(extractUser(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error while finding users", e);
        }
    }

    @Override
    public Optional<User> add(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User cannot be empty");
        }
        validator.validate(entity);
        String sql = "INSERT INTO users (first_name, last_name, email) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());
            stmt.setString(3, entity.getEmail());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long idx = generatedKeys.getLong(1);
                        entity.setId(idx);
                    }
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RepositoryException("Database error while adding user", e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }

        Optional<User> u = find(id);
        if (u.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
            return u;
        } catch (SQLException e) {
            throw new RepositoryException("Database error while deleting user", e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        validator.validate(entity);
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());
            stmt.setString(3, entity.getEmail());
            stmt.setLong(4, entity.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return Optional.empty();
            }
            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RepositoryException("Database error while updating user", e);
        }
    }
}
