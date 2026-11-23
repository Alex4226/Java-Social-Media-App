package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.Validator;
import repository.exceptions.RepositoryException;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL database repository implementation for {@link Friendship} entities.
 * Stores symmetric user relationships with canonical ID ordering ({@code id1 < id2}).
 */
public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructs a new {@code FriendshipDbRepository} with the given validator and database connection parameters.
     *
     * @param validator the validator used to validate {@link Friendship} entities before persistence operations
     * @param url       the JDBC URL used to connect to the PostgreSQL database
     * @param username  the username credential for authenticating with the database
     * @param password  the password credential for authenticating with the database
     */
    public FriendshipDbRepository(Validator<Friendship> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Normalizes a composite friendship ID so that the smaller user ID is always placed on the left ({@code id1 < id2}).
     *
     * @param id the {@link Tuple} containing the two user IDs
     * @return a normalized {@link Tuple} with {@code left <= right}
     * @throws IllegalArgumentException if {@code id} or either of its elements is {@code null}
     */
    private Tuple<Long, Long> normalizeId(Tuple<Long, Long> id) {
        if (id == null || id.getLeft() == null || id.getRight() == null) {
            throw new IllegalArgumentException("Friendship ID and its components must not be null");
        }
        return new Tuple<>(Math.min(id.getLeft(), id.getRight()), Math.max(id.getLeft(), id.getRight()));
    }

    /**
     * Extracts a {@link Friendship} entity from the current row of the given {@link ResultSet}.
     *
     * @param rs the {@link ResultSet} positioned at a valid friendship record
     * @return the constructed {@link Friendship} instance with a normalized composite ID
     * @throws SQLException if a database access error occurs or a column label is invalid
     */
    private Friendship extractFriendship(ResultSet rs) throws SQLException {
        long id1 = rs.getLong("id1");
        long id2 = rs.getLong("id2");
        Timestamp time = rs.getTimestamp("friends_from");
        LocalDateTime date = (time != null) ? time.toLocalDateTime() : null;
        return new Friendship(normalizeId(new Tuple<>(id1, id2)), date);
    }

    @Override
    public Optional<Friendship> find(Tuple<Long, Long> id) {
        Tuple<Long, Long> normalizedId = normalizeId(id);
        String sql = "SELECT id1, id2, friends_from FROM friendships WHERE id1 = ? AND id2 = ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, normalizedId.getLeft());
            stmt.setLong(2, normalizedId.getRight());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractFriendship(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error while finding friendship", e);
        }
    }

    @Override
    public Iterable<Friendship> getAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT id1, id2, friends_from FROM friendships";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                friendships.add(extractFriendship(rs));
            }
            return friendships;
        } catch (SQLException e) {
            throw new RepositoryException("Database error while retrieving friendships", e);
        }
    }

    @Override
    public Optional<Friendship> add(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship entity must not be null");
        }
        validator.validate(entity);
        Tuple<Long, Long> normalizedId = normalizeId(entity.getId());
        entity.setId(normalizedId);

        String sql = "INSERT INTO friendships (id1, id2, friends_from) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, normalizedId.getLeft());
            stmt.setLong(2, normalizedId.getRight());
            LocalDateTime date = (entity.getDate() != null) ? entity.getDate() : LocalDateTime.now();
            stmt.setTimestamp(3, Timestamp.valueOf(date));
            stmt.executeUpdate();
            return Optional.empty();
        } catch (SQLException e) {
            throw new RepositoryException("Database error while adding friendship", e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        Tuple<Long, Long> normalizedId = normalizeId(id);
        Optional<Friendship> existing = find(normalizedId);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, normalizedId.getLeft());
            stmt.setLong(2, normalizedId.getRight());
            stmt.executeUpdate();
            return existing;
        } catch (SQLException e) {
            throw new RepositoryException("Database error while deleting friendship", e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Friendship entity must not be null");
        }
        validator.validate(entity);
        Tuple<Long, Long> normalizedId = normalizeId(entity.getId());

        String sql = "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            LocalDateTime date = (entity.getDate() != null) ? entity.getDate() : LocalDateTime.now();
            stmt.setTimestamp(1, Timestamp.valueOf(date));
            stmt.setLong(2, normalizedId.getLeft());
            stmt.setLong(3, normalizedId.getRight());
            int rowsAffected = stmt.executeUpdate();
            return (rowsAffected > 0) ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RepositoryException("Database error while updating friendship", e);
        }
    }

    /**
     * Retrieves a paginated list of friends for a given user using a single SQL JOIN query.
     *
     * @param userId   the ID of the user whose friends are retrieved, must not be null
     * @param pageable pagination parameters specifying page number and page size, must not be null
     * @return a {@link Page} containing the user's friends on the requested page and the total number of friends
     * @throws IllegalArgumentException if {@code userId} or {@code pageable} is null
     * @throws RepositoryException      if a database access error occurs
     */
    public Page<User> findFriendsOnPage(Long userId, Pageable pageable) {
        if (userId == null || pageable == null) {
            throw new IllegalArgumentException("User ID and pageable cannot be null");
        }

        List<User> friends = new ArrayList<>();
        int totalCount = 0;
        int offset = pageable.getPageNumber() * pageable.getPageSize();

        String sql = "SELECT u.id, u.first_name, u.last_name, u.email, COUNT(*) OVER() AS total_count " +
                "FROM users u " +
                "JOIN friendships f ON (u.id = f.id1 OR u.id = f.id2) " +
                "WHERE (f.id1 = ? OR f.id2 = ?) AND u.id != ? " +
                "ORDER BY u.id " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            stmt.setLong(3, userId);
            stmt.setInt(4, pageable.getPageSize());
            stmt.setInt(5, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    totalCount = rs.getInt("total_count");
                    friends.add(new User(
                            rs.getLong("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    ));
                }
            }

            if (friends.isEmpty() && pageable.getPageNumber() > 0) {
                String countSql = "SELECT COUNT(*) AS count FROM friendships WHERE id1 = ? OR id2 = ?";
                try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                    countStmt.setLong(1, userId);
                    countStmt.setLong(2, userId);
                    try (ResultSet countRs = countStmt.executeQuery()) {
                        if (countRs.next()) {
                            totalCount = countRs.getInt("count");
                        }
                    }
                }
            }

            return new Page<>(friends, totalCount);

        } catch (SQLException e) {
            throw new RepositoryException("Database error while getting friendships page", e);
        }
    }
}