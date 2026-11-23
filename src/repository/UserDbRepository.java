package repository;

import domain.User;
import domain.validators.Validator;
import repository.exceptions.RepositoryException;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL database repository implementation for {@link User} entities.
 * Persists users using JDBC and retrieves database-generated primary keys.
 */
public class UserDbRepository implements PagingRepository<Long, User> {
    private final Validator<User> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructs a new {@code UserDbRepository} with the given validator and database connection parameters.
     *
     * @param validator the validator used to validate {@link User} entities before persistence operations
     * @param url       the JDBC URL used to connect to the PostgreSQL database
     * @param username  the username credential for authenticating with the database
     * @param password  the password credential for authenticating with the database
     */
    public UserDbRepository(Validator<User> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Extracts a {@link User} entity from the current row of the given {@link ResultSet}.
     *
     * @param rs the {@link ResultSet} positioned at a valid user record
     * @return the constructed {@link User} instance
     * @throws SQLException if a database access error occurs or a column label is invalid
     */
    private User extractUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");

        return new User(id, firstName, lastName, email);
    }

    @Override
    public Optional<User> find(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null!");
        }

        String query = "SELECT id, first_name, last_name, email FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractUser(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error while finding user", e);
        }
    }

    @Override
    public Iterable<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM users";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RepositoryException("Database error while finding users", e);
        }
    }

    @Override
    public Optional<User> add(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User cannot be null");
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
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("User and user ID cannot be null");
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

    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable must not be null!");
        }

        String countQuery = "SELECT COUNT(*) AS count FROM users";
        String selectQuery = "SELECT id, first_name, last_name, email FROM users ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            int totalCount = 0;
            try (PreparedStatement countStmt = conn.prepareStatement(countQuery);
                 ResultSet countRs = countStmt.executeQuery()) {
                if (countRs.next()) {
                    totalCount = countRs.getInt("count");
                }
            }

            int offset = pageable.getPageNumber() * pageable.getPageSize();
            if (totalCount == 0 || offset >= totalCount) {
                return new Page<>(Collections.emptyList(), totalCount);
            }

            List<User> users = new ArrayList<>();
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, pageable.getPageSize());
                selectStmt.setInt(2, offset);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        users.add(extractUser(rs));
                    }
                }
            }

            return new Page<>(users, totalCount);
        } catch (SQLException e) {
            throw new RepositoryException("Database error while getting users page", e);
        }
    }

}