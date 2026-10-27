package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;

import java.util.*;

/**
 * Service responsabil cu logica de business a retelei sociale, coordonand operatiile
 * asupra utilizatorilor, relatiilor de prietenie si algoritmilor de analiza a comunitatilor[cite: 1, 3].
 */
public class SocialNetworkService {
    private Repository<Long, User> userRepo;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepo;
    private Long currentUserId;

    /**
     * Constructor care injecteaza dependintele de repository necesare pentru utilizatori si prietenii[cite: 3].
     *
     * @param userRepo       repository-ul pentru gestionarea utilizatorilor[cite: 3]
     * @param friendshipRepo repository-ul pentru gestionarea relatiilor de prietenie[cite: 3]
     */
    public SocialNetworkService(Repository<Long, User> userRepo, Repository<Tuple<Long, Long>, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.currentUserId = computeNextUserId();
    }

    private Long computeNextUserId() {
        Long maxId = 0L;
        for (User user : userRepo.getAll()) {
            if (user.getId() != null & user.getId() > maxId) {
                maxId = user.getId();
            }
        }
        return maxId + 1;
    }

    /**
     * Adauga un utilizator nou in retea, generandu-i automat identificatorul unic[cite: 1, 3].
     *
     * @param firstName prenumele utilizatorului
     * @param lastName  numele de familie al utilizatorului
     * @param email     adresa de email a utilizatorului
     * @return utilizatorul creat si salvat[cite: 3]
     * @throws IllegalArgumentException daca exista deja un utilizator cu acelasi identificator
     * @throws domain.validators.ValidationException daca datele furnizate sunt invalide[cite: 3]
     */
    public User addUser(String firstName, String lastName, String email) {
        User user = new User(currentUserId, firstName, lastName, email);
        User result = userRepo.add(user);
        if (result != null) {
            throw new IllegalArgumentException("User with this ID already exists!");
        }
        currentUserId++;
        return user;
    }

    /**
     * Sterge un utilizator din sistem si elimina in cascada toate prieteniile asociate acestuia[cite: 1, 3].
     *
     * @param id identificatorul utilizatorului ce urmeaza a fi sters[cite: 1, 3]
     * @return entitatea User care a fost eliminata[cite: 3]
     * @throws IllegalArgumentException daca ID-ul este null sau utilizatorul nu exista
     */
    public User removeUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null!");
        }

        User deletedUser = userRepo.delete(id);
        if (deletedUser == null) {
            throw new IllegalArgumentException("User with ID " + id + " does not exist!");
        }

        java.util.List<Tuple<Long, Long>> friendshipsToDelete = new java.util.ArrayList<>();
        for (Friendship friendship : friendshipRepo.getAll()) {
            if (friendship.getLeftId().equals(id) || friendship.getRightId().equals(id)) {
                friendshipsToDelete.add(friendship.getId());
            }
        }

        for (Tuple<Long, Long> friendshipId : friendshipsToDelete) {
            friendshipRepo.delete(friendshipId);
        }

        return deletedUser;
    }

    /**
     * Cauta un utilizator dupa identificatorul sau unic.
     *
     * @param id identificatorul utilizatorului cautat
     * @return utilizatorul gasit sau {@code null} daca nu exista
     * @throws IllegalArgumentException daca identificatorul primit este null
     */
    public User findUser(Long id) {
        return userRepo.find(id);
    }

    /**
     * Returneaza toti utilizatorii inregistrati in retea.
     *
     * @return o colectie iterabila ce contine toti utilizatorii
     */
    public Iterable<User> getAllUsers() {
        return userRepo.getAll();
    }

    /**
     * Creeaza si salveaza o relatie de prietenie intre doi utilizatori existenti[cite: 1, 2, 3].
     *
     * @param id1 identificatorul primului utilizator[cite: 2]
     * @param id2 identificatorul celui de-al doilea utilizator[cite: 2]
     * @return relatia de prietenie adaugata[cite: 3]
     * @throws IllegalArgumentException daca ID-urile sunt nule, utilizatorii nu exista sau prietenia exista deja
     * @throws domain.validators.ValidationException daca datele relatiei nu sunt valide[cite: 3]
     */
    public Friendship addFriendship(Long id1, Long id2) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("IDs must not be null!");
        }

        if (userRepo.find(id1) == null) {
            throw new IllegalArgumentException("User with ID " + id1 + " does not exist!");
        }
        if (userRepo.find(id2) == null) {
            throw new IllegalArgumentException("User with ID " + id2 + " does not exist!");
        }

        Long minId = Math.min(id1, id2);
        Long maxId = Math.max(id1, id2);
        Tuple<Long, Long> tuple = new Tuple<>(minId, maxId);

        Friendship friendship = new Friendship(tuple);
        Friendship result = friendshipRepo.add(friendship);
        if (result != null) {
            throw new IllegalArgumentException("Friendship already exists!");
        }

        return friendship;
    }

    /**
     * Sterge relatia de prietenie dintre doi utilizatori[cite: 1, 2, 3].
     *
     * @param id1 identificatorul primului utilizator[cite: 2]
     * @param id2 identificatorul celui de-al doilea utilizator[cite: 2]
     * @return relatia de prietenie eliminata[cite: 3]
     * @throws IllegalArgumentException daca ID-urile sunt nule sau prietenia nu exista in sistem
     */
    public Friendship removeFriendship(Long id1, Long id2) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("IDs must not be null!");
        }

        Long minId = Math.min(id1, id2);
        Long maxId = Math.max(id1, id2);
        Tuple<Long, Long> tuple = new Tuple<>(minId, maxId);

        Friendship deleted = friendshipRepo.delete(tuple);
        if (deleted == null) {
            throw new IllegalArgumentException("Friendship does not exist!");
        }

        return deleted;
    }

    /**
     * Returneaza toate relatiile de prietenie din retea.
     *
     * @return o colectie iterabila ce contine toate prieteniile
     */
    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepo.getAll();
    }

    /**
     * Construieste lista de adiacenta a grafului pe baza utilizatorilor si prieteniilor existente[cite: 2].
     *
     * @return un dictionar ce mapeaza fiecare ID de utilizator la lista prietenilor sai[cite: 2]
     */
    private Map<Long, List<Long>> buildAdjacencyList() {
        Map<Long, List<Long>> adjList = new HashMap<>();

        for (User user : userRepo.getAll()) {
            adjList.put(user.getId(), new ArrayList<>());
        }

        for (Friendship friendship : friendshipRepo.getAll()) {
            Long u1 = friendship.getLeftId();
            Long u2 = friendship.getRightId();

            if (adjList.containsKey(u1) && adjList.containsKey(u2)) {
                adjList.get(u1).add(u2);
                adjList.get(u2).add(u1);
            }
        }

        return adjList;
    }

    /**
     * Parcurgere in adancime (DFS) pentru a colecta toate nodurile unei componente conexe[cite: 1, 3].
     *
     * @param current   nodul curent vizitat in parcurgere
     * @param adjList   lista de adiacenta a grafului
     * @param visited   setul de noduri deja vizitate
     * @param component lista in care se adauga nodurile componentei conexe curente[cite: 1]
     */
    private void dfsCollectComponent(Long current, Map<Long, List<Long>> adjList, Set<Long> visited, List<Long> component) {
        visited.add(current);
        component.add(current);

        for (Long neighbor : adjList.get(current)) {
            if (!visited.contains(neighbor)) {
                dfsCollectComponent(neighbor, adjList, visited, component);
            }
        }
    }

    /**
     * Determina toate componentele conexe din graful retelei sociale[cite: 1, 3].
     *
     * @return o lista de liste, fiecare sublista continand ID-urile utilizatorilor dintr-o comunitate[cite: 1, 3]
     */
    private List<List<Long>> getAllComponents() {
        Map<Long, List<Long>> adjList = buildAdjacencyList();
        Set<Long> visited = new HashSet<>();
        List<List<Long>> components = new ArrayList<>();

        for (Long userId : adjList.keySet()) {
            if (!visited.contains(userId)) {
                List<Long> component = new ArrayList<>();
                dfsCollectComponent(userId, adjList, visited, component);
                components.add(component);
            }
        }

        return components;
    }

    /**
     * Calculeaza numarul total de comunitati (componente conexe) prezente in retea[cite: 1, 3].
     *
     * @return numarul de comunitati determinate[cite: 1, 3]
     */
    public int getNumberOfCommunities() {
        return getAllComponents().size();
    }

    /**
     * Determina recursiv lungimea celui mai lung drum simplu pornind dintr-un nod dat, utilizand DFS cu backtracking[cite: 3].
     *
     * @param current       nodul curent de pe drum
     * @param adjList       lista de adiacenta a grafului
     * @param visitedOnPath setul nodurilor vizitate pe drumul curent
     * @return lungimea maxima a drumului simplu gasit din nodul curent[cite: 3]
     */
    private int dfsLongestPathFromNode(Long current, Map<Long, List<Long>> adjList, Set<Long> visitedOnPath) {
        visitedOnPath.add(current);
        int maxLen = 0;

        for (Long neighbor : adjList.get(current)) {
            if (!visitedOnPath.contains(neighbor)) {
                int len = 1 + dfsLongestPathFromNode(neighbor, adjList, visitedOnPath);
                if (len > maxLen) {
                    maxLen = len;
                }
            }
        }

        visitedOnPath.remove(current); // Backtracking
        return maxLen;
    }

    /**
     * Determina cea mai sociabila comunitate din retea, definita ca fiind componenta conexa cu cel mai lung drum simplu[cite: 3].
     *
     * @return o lista de utilizatori care alcatuiesc cea mai sociabila comunitate[cite: 3]
     */
    public List<User> getMostSociableCommunity() {
        List<List<Long>> components = getAllComponents();
        if (components.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<Long>> adjList = buildAdjacencyList();
        List<Long> bestComponent = new ArrayList<>();
        int maxPathLength = -1;

        for (List<Long> component : components) {
            int longestPathInComponent = 0;

            for (Long node : component) {
                Set<Long> visitedOnPath = new HashSet<>();
                int pathLength = dfsLongestPathFromNode(node, adjList, visitedOnPath);
                if (pathLength > longestPathInComponent) {
                    longestPathInComponent = pathLength;
                }
            }

            if (longestPathInComponent > maxPathLength) {
                maxPathLength = longestPathInComponent;
                bestComponent = component;
            }
        }

        List<User> result = new ArrayList<>();
        for (Long id : bestComponent) {
            result.add(userRepo.find(id));
        }

        return result;
    }

}