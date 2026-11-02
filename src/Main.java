import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import domain.validators.ValidationException;
import domain.validators.Validator;
import repository.Repository;
import repository.RepositoryFactory;
import service.SocialNetworkService;
import ui.ConsoleUI;

/**
 * Application entry point responsible for wiring dependencies,
 * configuring validators using functional composition, and launching the UI.
 */
public class Main {

    /**
     * Bootstraps the layered architecture and starts the console loop.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        // Base validator from Lab 3 (unmodified strategy)
        Validator<User> baseUserValidator = new UserValidator();

        // New validation rule defined strictly as a lambda expression without new classes
        Validator<User> nameCapitalizationValidator = user -> {
            if (user != null) {
                if (user.getFirstName() != null && !Character.isUpperCase(user.getFirstName().charAt(0))) {
                    throw new ValidationException("First name must start with a capital letter!");
                }
                if (user.getLastName() != null && !Character.isUpperCase(user.getLastName().charAt(0))) {
                    throw new ValidationException("Last name must start with a capital letter!");
                }
            }
        };

        // Composed validator using default andThen method
        Validator<User> composedUserValidator = baseUserValidator.andThen(nameCapitalizationValidator);

        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        RepositoryFactory factory = new RepositoryFactory("application.properties");

        Repository<Long, User> userRepository = factory.createUserRepository(composedUserValidator);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = factory.createFriendshipRepository(friendshipValidator);

        SocialNetworkService service = new SocialNetworkService(userRepository, friendshipRepository);
        ConsoleUI console = new ConsoleUI(service);

        console.run();
    }
}