package ui;

import domain.Friendship;
import domain.User;
import domain.validators.ValidationException;
import repository.exceptions.RepositoryException;
import service.SocialNetworkService;

import java.util.List;
import java.util.Scanner;

/**
 * Text-based user interface responsible for command handling,
 * reading user input, and displaying output messages.
 */
public class ConsoleUI {
    private final SocialNetworkService service;
    private final Scanner scanner;

    /**
     * Constructs a new ConsoleUI instance.
     *
     * @param service the social network service used to execute operations
     */
    public ConsoleUI(SocialNetworkService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
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
        for (User user : service.getAllUsers()) {
            System.out.println(user);
        }
    }

    private void uiShowFriendships() {
        System.out.println("--- Lista Prietenii ---");
        Iterable<Friendship> friendships = service.getAllFriendships();
        boolean hasAny = false;
        for (Friendship friendship : friendships) {
            System.out.println(friendship);
            hasAny = true;
        }
        if (!hasAny) {
            System.out.println("Nu exista prietenii inregistrate.");
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
        for (User user : community) {
            System.out.println(user);
        }
    }
}