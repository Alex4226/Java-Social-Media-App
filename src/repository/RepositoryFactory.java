package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.Validator;
import repository.exceptions.RepositoryException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * Factory class responsible for instantiating the appropriate repository implementations
 * based on configuration properties, using functional mappings instead of conditional branching.
 */
public class RepositoryFactory {
    private final Properties properties = new Properties();
    private final Map<String, Function<Validator<User>, Repository<Long, User>>> userRepoCreators = new HashMap<>();
    private final Map<String, Function<Validator<Friendship>, Repository<Tuple<Long, Long>, Friendship>>> friendshipRepoCreators = new HashMap<>();

    /**
     * Initializes the factory by loading settings from the given properties file path
     * and configuring the creation strategies.
     *
     * @param configPath path to the configuration file
     * @throws RepositoryException if configuration cannot be loaded
     */
    public RepositoryFactory(String configPath) {
        try (InputStream input = new FileInputStream(configPath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RepositoryException("Failed to load configuration properties from: " + configPath, e);
        }

        initCreators();
    }

    /**
     * Registers the repository creation strategies inside functional maps[cite: 1].
     */
    private void initCreators() {
        // User repository strategies
        userRepoCreators.put("memory", InMemoryRepository::new); // Constructor reference[cite: 1]
        userRepoCreators.put("file", validator -> {
            String userFile = properties.getProperty("data.users", "data/utilizatori.txt");
            UserFileRepository fileRepo = new UserFileRepository(validator, userFile);
            fileRepo.loadData();
            return fileRepo;
        });
        userRepoCreators.put("db", validator -> {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String pass = properties.getProperty("db.password");
            return new UserDbRepository(validator, url, user, pass);
        });

        // Friendship repository strategies
        friendshipRepoCreators.put("memory", InMemoryRepository::new); // Constructor reference[cite: 1]
        friendshipRepoCreators.put("file", validator -> {
            String friendshipFile = properties.getProperty("data.friendships", "data/prietenii.txt");
            FriendshipFileRepository fileRepo = new FriendshipFileRepository(validator, friendshipFile);
            fileRepo.loadData();
            return fileRepo;
        });
        friendshipRepoCreators.put("db", validator -> {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String pass = properties.getProperty("db.password");
            return new FriendshipDbRepository(validator, url, user, pass);
        });
    }

    /**
     * Creates and returns a repository for User entities based on the configured repository type[cite: 1].
     *
     * @param validator the validator to use
     * @return initialized User repository
     */
    public Repository<Long, User> createUserRepository(Validator<User> validator) {
        String repoType = properties.getProperty("repository.type", "memory").trim().toLowerCase();
        return Optional.ofNullable(userRepoCreators.get(repoType))
                .orElse(userRepoCreators.get("memory"))
                .apply(validator);
    }

    /**
     * Creates and returns a repository for Friendship entities based on the configured repository type[cite: 1].
     *
     * @param validator the validator to use
     * @return initialized Friendship repository
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        String repoType = properties.getProperty("repository.type", "memory").trim().toLowerCase();
        return Optional.ofNullable(friendshipRepoCreators.get(repoType))
                .orElse(friendshipRepoCreators.get("memory"))
                .apply(validator);
    }
}