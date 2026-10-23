import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import repository.InMemoryRepository;
import repository.Repository;
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
        UserValidator userValidator = new UserValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();

        Repository<Long, User> userRepo = new InMemoryRepository<>(userValidator);
        Repository<Tuple<Long, Long>, Friendship> friendshipRepo = new InMemoryRepository<>(friendshipValidator);

        SocialNetworkService service = new SocialNetworkService(userRepo, friendshipRepo);

        service.addUser("Ion", "Popescu", "ion@test.com");
        service.addUser("Maria", "Ionescu", "maria@test.com");
        service.addUser("Andrei", "Radu", "andrei@test.com");
        service.addUser("Elena", "Stan", "elena@test.com");
        service.addFriendship(1L, 2L);
        service.addFriendship(2L, 3L);

        ConsoleUI ui = new ConsoleUI(service);
        ui.run();
    }
}