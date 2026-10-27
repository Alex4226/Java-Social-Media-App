import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import domain.validators.Validator;
import repository.Repository;
import repository.RepositoryFactory;
import service.SocialNetworkService;
import ui.ConsoleUI;

/**
 * Punctul principal de intrare in aplicatie.
 * Responsabil pentru instantierea componentelor, injectarea dependintelor
 * si pornirea interfetei cu utilizatorul[cite: 3].
 */
public class Main {
    /**
     * Metoda principala care configureaza arhitectura stratificata a aplicatiei si porneste interfata text[cite: 3].
     *
     * @param args argumentele primite din linia de comanda
     */
    public static void main(String[] args) {
        Validator<User> userValidator = new UserValidator();
        Validator<Friendship> friendshipValidator = new FriendshipValidator();

        RepositoryFactory factory = new RepositoryFactory("application.properties");

        Repository<Long, User> userRepository = factory.createUserRepository(userValidator);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository = factory.createFriendshipRepository(friendshipValidator);

        SocialNetworkService service = new SocialNetworkService(userRepository, friendshipRepository);
        ConsoleUI console = new ConsoleUI(service);

        console.run();
    }
}