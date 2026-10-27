package repository;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.Validator;
import repository.exceptions.RepositoryException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Factory class responsible for instantiating the appropriate repository implementations
 * based on configuration properties.
 */
public class RepositoryFactory {
    private final Properties properties = new Properties();

    /**
     * Initializes the factory by loading settings from the given properties file path.
     * @param configPath path to the configuration file
     */
    public RepositoryFactory(String configPath) {
        try (InputStream input = new FileInputStream(configPath)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RepositoryException("Failed to load configuration properties from: " + configPath, e);
        }
    }

    /**
     * Creates and returns a repository for User entities based on the configured repository type.
     * @param validator the validator to use
     * @return initialized User repository
     */
    public Repository<Long, User> createUserRepository(Validator<User> validator) {
        String repoType = properties.getProperty("repository.type", "memory").trim().toLowerCase();

        switch (repoType) {
            case "file":
                String userFile = properties.getProperty("data.users", "data/utilizatori.txt");
                UserFileRepository fileRepo = new UserFileRepository(validator, userFile);
                fileRepo.loadData();
                return fileRepo;
            case "db":
                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.username");
                String pass = properties.getProperty("db.password");
                return new UserDbRepository(validator, url, user, pass);
            case "memory":
            default:
                return new InMemoryRepository<>(validator);
        }
    }

    /**
     * Creates and returns a repository for Friendship entities based on the configured repository type.
     * @param validator the validator to use
     * @return initialized Friendship repository
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator) {
        String repoType = properties.getProperty("repository.type", "memory").trim().toLowerCase();

        switch (repoType) {
            case "file":
                String friendshipFile = properties.getProperty("data.friendships", "data/prietenii.txt");
                FriendshipFileRepository fileRepo = new FriendshipFileRepository(validator, friendshipFile);
                fileRepo.loadData();
                return fileRepo;
            case "db":
                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.username");
                String pass = properties.getProperty("db.password");
                return new FriendshipDbRepository(validator, url, user, pass);
            case "memory":
            default:
                return new InMemoryRepository<>(validator);
        }
    }
}