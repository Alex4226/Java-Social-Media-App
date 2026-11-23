package ui;

import domain.CriteriiUtilizator;
import domain.Friendship;
import domain.User;
import domain.validators.ValidationException;
import repository.exceptions.RepositoryException;
import service.SocialNetworkService;

import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;
import java.util.function.Function;
import util.paging.Page;
import util.paging.Pageable;

/**
 * Text-based user interface responsible for command handling,
 * reading user input, and displaying output messages using functional paradigms[cite: 1, 3].
 */
public class ConsoleUI {
    private final SocialNetworkService service;
    private final Scanner scanner;
    private final int pageSize;

    /**
     * Constructs a new ConsoleUI instance.
     *
     * @param service the social network service used to execute operations
     * @param pageSize the number of elements displayed per page
     */
    public ConsoleUI(SocialNetworkService service, int pageSize) {
        this.service = service;
        this.scanner = new Scanner(System.in);
        this.pageSize = pageSize;
    }

    /**
     * Starts the main interaction loop of the console application.
     */
    public void run() {
        while (true) {
            System.out.println("\n--- Social Network Menu ---");
            System.out.println("1. Adauga utilizator");
            System.out.println("2. Sterge utilizator");
            System.out.println("3. Adauga prieten");
            System.out.println("4. Sterge prieten");
            System.out.println("5. Afiseaza toti utilizatorii");
            System.out.println("6. Afiseaza toate prieteniile");
            System.out.println("7. Numar de comunitati");
            System.out.println("8. Cea mai sociabila comunitate");
            System.out.println("9. Filtreaza utilizatori (demonstratie Predicate)");
            System.out.println("10. Afiseaza utilizatorii (paginat)");
            System.out.println("11. Afiseaza prietenii unui utilizator (paginat)");
            System.out.println("0. Iesire");
            System.out.print("Optiune: ");

            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1":
                        uiAddUser();
                        break;
                    case "2":
                        uiRemoveUser();
                        break;
                    case "3":
                        uiAddFriend();
                        break;
                    case "4":
                        uiRemoveFriend();
                        break;
                    case "5":
                        uiShowUsers();
                        break;
                    case "6":
                        uiShowFriendships();
                        break;
                    case "7":
                        uiShowCommunitiesCount();
                        break;
                    case "8":
                        uiShowMostSociableCommunity();
                        break;
                    case "9":
                        uiFilterUsers();
                        break;
                    case "10":
                        uiShowUsersPaginated();
                        break;
                    case "11":
                        uiShowFriendsPaginated();
                        break;
                    case "0":
                        System.out.println("Aplicatia se inchide.");
                        return;
                    default:
                        System.out.println("Optiune invalida!");
                        break;
                }
            } catch (ValidationException | IllegalArgumentException | RepositoryException e) {
                System.out.println("Eroare: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Eroare neasteptata: " + e.getMessage());
            }
        }
    }

    private void uiAddUser() {
        System.out.print("Prenume: ");
        String firstName = scanner.nextLine();
        System.out.print("Nume: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        User user = service.addUser(firstName, lastName, email);
        System.out.println("Utilizator adaugat: " + user);
    }

    private void uiRemoveUser() {
        System.out.print("ID utilizator de sters: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        User user = service.removeUser(id);
        System.out.println("Utilizator sters: " + user);
    }

    private void uiAddFriend() {
        System.out.print("ID utilizator 1: ");
        Long id1 = Long.parseLong(scanner.nextLine().trim());
        System.out.print("ID utilizator 2: ");
        Long id2 = Long.parseLong(scanner.nextLine().trim());

        service.addFriendship(id1, id2);
        System.out.println("Prietenie adaugata cu succes!");
    }

    private void uiRemoveFriend() {
        System.out.print("ID utilizator 1: ");
        Long id1 = Long.parseLong(scanner.nextLine().trim());
        System.out.print("ID utilizator 2: ");
        Long id2 = Long.parseLong(scanner.nextLine().trim());

        service.removeFriendship(id1, id2);
        System.out.println("Prietenie stearsa cu succes!");
    }

    private void uiShowUsers() {
        System.out.println("--- Lista Utilizatori ---");
        StreamSupport.stream(service.getAllUsers().spliterator(), false)
                .forEach(System.out::println); // Instance method reference on a particular object
    }

    private void uiShowFriendships() {
        System.out.println("--- Lista Prietenii ---");
        List<Friendship> friendships = StreamSupport.stream(service.getAllFriendships().spliterator(), false)
                .toList();

        if (friendships.isEmpty()) {
            System.out.println("Nu exista prietenii inregistrate.");
        } else {
            friendships.forEach(System.out::println);
        }
    }

    private void uiShowCommunitiesCount() {
        int count = service.getNumberOfCommunities();
        System.out.println("Numar de comunitati (componente conexe): " + count);
    }

    private void uiShowMostSociableCommunity() {
        List<User> community = service.getMostSociableCommunity();
        System.out.println("--- Cea mai sociabila comunitate (cel mai lung drum) ---");
        if (community.isEmpty()) {
            System.out.println("Nu exista comunitati in retea.");
            return;
        }
        community.forEach(System.out::println);
    }

    private void uiFilterUsers() {
        System.out.println("Selectati criteriul de filtrare:");
        System.out.println("a. Utilizatori dupa prefixul numelui de familie");
        System.out.println("b. Utilizatori dupa domeniul de email");
        System.out.println("c. Utilizatori fara prieteni");
        System.out.print("Alegere: ");
        String choice = scanner.nextLine().trim().toLowerCase();

        Predicate<User> predicate;
        switch (choice) {
            case "a":
                System.out.print("Introduceti prefixul: ");
                String prefix = scanner.nextLine().trim();
                predicate = CriteriiUtilizator.byLastNamePrefix(prefix);
                break;
            case "b":
                System.out.print("Introduceti domeniul (ex: scs.ubbcluj.ro): ");
                String domain = scanner.nextLine().trim();
                predicate = CriteriiUtilizator.byEmailDomain(domain);
                break;
            case "c":
                predicate = CriteriiUtilizator::faraPrieteni; // Static method reference[cite: 1]
                break;
            default:
                System.out.println("Criteriu invalid!");
                return;
        }

        List<User> filtered = service.findUsersMatching(predicate);
        if (filtered.isEmpty()) {
            System.out.println("Niciun utilizator nu corespunde criteriului.");
        } else {
            System.out.println("--- Rezultate filtrare ---");
            filtered.forEach(System.out::println);
        }
    }

    private void uiShowUsersPaginated() {
        paginate(service::getUsersOnPage, "Utilizatori (Paginat)");
    }

    private void uiShowFriendsPaginated() {
        System.out.print("ID utilizator: ");
        Long userId = Long.parseLong(scanner.nextLine().trim());
        paginate(pageable -> service.getFriendsOnPage(userId, pageable),
                "Prietenii utilizatorului " + userId + " (Paginat)");
    }

    private void paginate(Function<Pageable, Page<User>> pageFetcher, String title) {
        int currentPage = 0;

        while (true) {
            Pageable pageable = new Pageable(currentPage, pageSize);
            Page<User> page = pageFetcher.apply(pageable);

            int totalElements = page.getTotalNumberOfElements();
            int totalPages = (int) Math.ceil((double) totalElements / pageSize);
            List<User> elements = StreamSupport.stream(page.getElementsOnPage().spliterator(), false).toList();

            System.out.println("\n--- " + title + " ---");
            if (elements.isEmpty()) {
                System.out.println("Nu exista elemente pe aceasta pagina.");
            } else {
                elements.forEach(System.out::println);
            }

            int displayPage = (totalPages == 0) ? 0 : (currentPage + 1);
            System.out.println("Pagina " + displayPage + " din " + totalPages + " (Total: " + totalElements + ")");
            System.out.print("[n] Inainte | [p] Inapoi | [g] Mergi la pagina | [x] Iesire: ");

            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "n":
                    if (currentPage + 1 < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("Esti deja pe ultima pagina.");
                    }
                    break;
                case "p":
                    if (currentPage > 0) {
                        currentPage--;
                    } else {
                        System.out.println("Esti deja pe prima pagina.");
                    }
                    break;
                case "g":
                    System.out.print("Numar pagina: ");
                    int targetPage = Integer.parseInt(scanner.nextLine().trim());
                    currentPage = targetPage - 1;
                    break;
                case "x":
                    return;
                default:
                    System.out.println("Comanda invalida!");
                    break;
            }
        }
    }
}