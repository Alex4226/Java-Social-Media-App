package ui;

import domain.User;
import domain.validators.ValidationException;
import service.SocialNetworkService;

import java.util.List;
import java.util.Scanner;

/**
 * Interfata cu utilizatorul de tip consola, responsabila pentru interactiunea directa,
 * citirea comenzilor si afisarea rezultatelor[cite: 3].
 */
public class ConsoleUI {
    private final SocialNetworkService service;
    private final Scanner scanner;

    /**
     * Constructor care initializeaza interfata de consola cu instanta corespunzatoare de service.
     *
     * @param service serviciul de retea sociala utilizat pentru executarea operatiilor
     */
    public ConsoleUI(SocialNetworkService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Porneste bucla principala a aplicatiei de consola si trateaza optiunile selectate de utilizator.
     */
    public void run() {
        while (true) {
            System.out.println("\n--- Social Network Menu ---");
            System.out.println("1. Adauga utilizator");
            System.out.println("2. Sterge utilizator");
            System.out.println("3. Adauga prieten");
            System.out.println("4. Sterge prieten");
            System.out.println("5. Afiseaza toti utilizatorii");
            System.out.println("6. Numar de comunitati");
            System.out.println("7. Cea mai sociabila comunitate");
            System.out.println("0. Iesire");
            System.out.print("Optiune: ");

            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1" -> uiAddUser();
                    case "2" -> uiRemoveUser();
                    case "3" -> uiAddFriend();
                    case "4" -> uiRemoveFriend();
                    case "5" -> uiShowUsers();
                    case "6" -> uiShowCommunitiesCount();
                    case "7" -> uiShowMostSociableCommunity();
                    case "0" -> {
                        System.out.println("Aplicatia se inchide.");
                        return;
                    }
                    default -> System.out.println("Optiune invalida!");
                }
            } catch (ValidationException | IllegalArgumentException e) {
                System.out.println("Eroare: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Eroare neasteptata: " + e.getMessage());
            }
        }
    }

    /**
     * Citeste datele de la tastatura si apeleaza serviciul pentru adaugarea unui utilizator nou[cite: 3].
     */
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

    /**
     * Citeste identificatorul de la tastatura si apeleaza serviciul pentru stergerea utilizatorului[cite: 3].
     */
    private void uiRemoveUser() {
        System.out.print("ID utilizator de sters: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = service.removeUser(id);
        System.out.println("Utilizator sters: " + user);
    }

    /**
     * Citeste identificatorii a doi utilizatori si apeleaza serviciul pentru crearea unei prietenii[cite: 2, 3].
     */
    private void uiAddFriend() {
        System.out.print("ID utilizator 1: ");
        Long id1 = Long.parseLong(scanner.nextLine());
        System.out.print("ID utilizator 2: ");
        Long id2 = Long.parseLong(scanner.nextLine());

        service.addFriendship(id1, id2);
        System.out.println("Prietenie adaugata cu succes!");
    }

    /**
     * Citeste identificatorii a doi utilizatori si apeleaza serviciul pentru stergerea prieteniei[cite: 2, 3].
     */
    private void uiRemoveFriend() {
        System.out.print("ID utilizator 1: ");
        Long id1 = Long.parseLong(scanner.nextLine());
        System.out.print("ID utilizator 2: ");
        Long id2 = Long.parseLong(scanner.nextLine());

        service.removeFriendship(id1, id2);
        System.out.println("Prietenie stearsa cu succes!");
    }

    /**
     * Afiseaza lista completa a utilizatorilor inregistrati in retea.
     */
    private void uiShowUsers() {
        System.out.println("--- Lista Utilizatori ---");
        for (User user : service.getAllUsers()) {
            System.out.println(user);
        }
    }

    /**
     * Solicita si afiseaza numarul total de comunitati (componente conexe) din retea[cite: 3].
     */
    private void uiShowCommunitiesCount() {
        int count = service.getNumberOfCommunities();
        System.out.println("Numar de comunitati (componente conexe): " + count);
    }

    /**
     * Solicita si afiseaza utilizatorii care fac parte din cea mai sociabila comunitate[cite: 3].
     */
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