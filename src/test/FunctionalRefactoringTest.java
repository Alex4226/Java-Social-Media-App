package test;

import domain.CriteriiUtilizator;
import domain.Friendship;
import domain.Tuple;
import domain.User;
import domain.validators.ValidationException;
import domain.validators.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InMemoryRepository;
import repository.Repository;
import service.SocialNetworkService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionalRefactoringTest {
    private Repository<Long, User> userRepo;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepo;
    private SocialNetworkService service;

    @BeforeEach
    void setUp() {
        Validator<User> baseValidator = user -> {
            if (user == null || user.getFirstName() == null || user.getLastName() == null) {
                throw new ValidationException("Invalid basic fields");
            }
        };

        // Regula noua compusa prin andThen
        Validator<User> capitalizedValidator = user -> {
            if (!Character.isUpperCase(user.getFirstName().charAt(0))) {
                throw new ValidationException("First letter must be uppercase");
            }
        };

        Validator<User> composedValidator = baseValidator.andThen(capitalizedValidator);
        Validator<Friendship> friendshipValidator = friendship -> {};

        userRepo = new InMemoryRepository<>(composedValidator);
        friendshipRepo = new InMemoryRepository<>(friendshipValidator);
        service = new SocialNetworkService(userRepo, friendshipRepo);
    }

    @Test
    void testValidatorComposition() {
        // Testeaza esecul pe regula lambda noua
        assertThrows(ValidationException.class, () -> service.addUser("alex", "Pop", "alex@scs.ro"));

        // Testeaza succesul pe ambele reguli
        assertDoesNotThrow(() -> service.addUser("Alex", "Pop", "alex@scs.ro"));
    }

    @Test
    void testPredicatesAndFiltering() {
        User u1 = service.addUser("Alex", "Pop", "alex@scs.ro");
        User u2 = service.addUser("Dan", "Muresan", "dan@gmail.com");

        // Filtrare dupa prefix
        List<User> byPrefix = service.findUsersMatching(CriteriiUtilizator.byLastNamePrefix("Po"));
        assertEquals(1, byPrefix.size());
        assertEquals("Pop", byPrefix.get(0).getLastName());

        // Filtrare dupa domeniu
        List<User> byDomain = service.findUsersMatching(CriteriiUtilizator.byEmailDomain("scs.ro"));
        assertEquals(1, byDomain.size());

        // Fara prieteni initial
        List<User> lonely = service.findUsersMatching(CriteriiUtilizator::faraPrieteni);
        assertEquals(2, lonely.size());

        // Dupa imprietenire
        service.addFriendship(u1.getId(), u2.getId());
        lonely = service.findUsersMatching(CriteriiUtilizator::faraPrieteni);
        assertEquals(0, lonely.size());
    }

    @Test
    void testCascadeDeletionAndOptionalHandling() {
        User u1 = service.addUser("Alex", "Pop", "alex@scs.ro");
        User u2 = service.addUser("Dan", "Muresan", "dan@gmail.com");
        service.addFriendship(u1.getId(), u2.getId());

        service.removeUser(u1.getId());

        // Optional empty la cautarea utilizatorului sters
        assertTrue(service.findUser(u1.getId()).isEmpty());
        assertTrue(service.findUser(u2.getId()).isPresent());

        // Prietenia s-a sters in cascada
        assertFalse(service.getAllFriendships().iterator().hasNext());
    }
}