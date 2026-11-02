package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Service class orchestrating social network business logic, user relationships,
 * and graph-based community analytics using Java 8 Stream API.
 */
public class SocialNetworkService {
    private final Repository<Long, User> userRepo;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepo;
    private Long currentUserId;

    /**
     * Initializes the service with injected repositories for users and friendships,
     * computes the initial sequence ID, and synchronizes friendship lists in memory.
     *
     * @param userRepo       the repository used to manage User entities
     * @param friendshipRepo the repository used to manage Friendship entities
     */
    public SocialNetworkService(Repository<Long, User> userRepo, Repository<Tuple<Long, Long>, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.currentUserId = computeNextUserId();
        syncFriendsLists();
    }

    /**
     * Synchronizes friends lists for all existing users from friendship repository records.
     */
    private void syncFriendsLists() {
        toStream(friendshipRepo.getAll()).forEach(friendship -> {
            Optional<User> user1Opt = findUser(friendship.getLeftId());
            Optional<User> user2Opt = findUser(friendship.getRightId());

            if (user1Opt.isPresent() && user2Opt.isPresent()) {
                User u1 = user1Opt.get();
                User u2 = user2Opt.get();
                u1.addFriend(u2);
                u2.addFriend(u1);
            }
        });
    }

    /**
     * Helper utility converting an Iterable collection into a sequential Stream.
     *
     * @param <T>      the element type of the iterable
     * @param iterable the source iterable to stream
     * @return a sequential stream of elements from the source iterable
     */
    private <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Calculates the next sequential identifier available for user registration using Stream API.
     *
     * @return the next available unique identifier as a Long
     */
    private Long computeNextUserId() {
        return toStream(userRepo.getAll())
                .map(User::getId)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .map(maxId -> maxId + 1)
                .orElse(1L);
    }

    /**
     * Adds a new user to the network and assigns an auto-generated identifier.
     *
     * @param firstName user's first name
     * @param lastName  user's last name
     * @param email     user's email address
     * @return the saved User entity
     * @throws IllegalArgumentException if a user with the generated ID already exists
     * @throws domain.validators.ValidationException if user attributes violate validation constraints
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
     * Removes a user and cascades the removal over all connected friendships using Streams,
     * maintaining bidirectional friendship list consistency in memory.
     *
     * @param id identifier of the user to remove
     * @return the removed User entity
     * @throws IllegalArgumentException if the identifier is null or no user exists with the given id
     */
    public User removeUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null!");
        }

        User deletedUser = Optional.ofNullable(userRepo.delete(id))
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " does not exist!"));

        // Clean memory references from remaining friends
        new ArrayList<>(deletedUser.getFriends()).forEach(friend -> friend.removeFriend(deletedUser));
        deletedUser.getFriends().clear();

        // Cascade deletion in the persistence repository
        toStream(friendshipRepo.getAll())
                .filter(f -> f.getLeftId().equals(id) || f.getRightId().equals(id))
                .map(Friendship::getId)
                .collect(Collectors.toList())
                .forEach(friendshipRepo::delete);

        return deletedUser;
    }

    /**
     * Finds a user wrapped inside an Optional.
     *
     * @param id unique identifier of the user to locate
     * @return an Optional containing the found user, or an empty Optional if no user exists with the specified id
     * @throws IllegalArgumentException if the provided id is null
     */
    public Optional<User> findUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        return Optional.ofNullable(userRepo.find(id));
    }

    /**
     * Retrieves all registered users as an Iterable.
     *
     * @return an Iterable containing all users in the network
     */
    public Iterable<User> getAllUsers() {
        return userRepo.getAll();
    }

    /**
     * Establishes a friendship between two existing users and synchronizes in-memory friend lists.
     *
     * @param id1 first user identifier
     * @param id2 second user identifier
     * @return the newly created Friendship entity
     * @throws IllegalArgumentException if any ID is null, any user does not exist, or the friendship already exists
     * @throws domain.validators.ValidationException if the friendship entity fails domain validation
     */
    public Friendship addFriendship(Long id1, Long id2) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("IDs must not be null!");
        }

        User u1 = findUser(id1).orElseThrow(() -> new IllegalArgumentException("User with ID " + id1 + " does not exist!"));
        User u2 = findUser(id2).orElseThrow(() -> new IllegalArgumentException("User with ID " + id2 + " does not exist!"));

        Long minId = Math.min(id1, id2);
        Long maxId = Math.max(id1, id2);
        Tuple<Long, Long> tuple = new Tuple<>(minId, maxId);

        Friendship friendship = new Friendship(tuple);
        Friendship result = friendshipRepo.add(friendship);
        if (result != null) {
            throw new IllegalArgumentException("Friendship already exists!");
        }

        u1.addFriend(u2);
        u2.addFriend(u1);

        return friendship;
    }

    /**
     * Removes an established friendship between two users and updates in-memory friend lists.
     *
     * @param id1 first user identifier
     * @param id2 second user identifier
     * @return the removed Friendship entity
     * @throws IllegalArgumentException if any ID is null or the friendship does not exist in the network
     */
    public Friendship removeFriendship(Long id1, Long id2) {
        if (id1 == null || id2 == null) {
            throw new IllegalArgumentException("IDs must not be null!");
        }

        Long minId = Math.min(id1, id2);
        Long maxId = Math.max(id1, id2);
        Tuple<Long, Long> tuple = new Tuple<>(minId, maxId);

        Friendship deleted = Optional.ofNullable(friendshipRepo.delete(tuple))
                .orElseThrow(() -> new IllegalArgumentException("Friendship does not exist!"));

        findUser(id1).ifPresent(u1 -> findUser(id2).ifPresent(u1::removeFriend));
        findUser(id2).ifPresent(u2 -> findUser(id1).ifPresent(u2::removeFriend));

        return deleted;
    }

    /**
     * Retrieves all friendships stored in the network.
     *
     * @return an Iterable containing all friendship relations
     */
    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepo.getAll();
    }

    /**
     * Constructs the adjacency list representation of the network graph using Streams.
     *
     * @return a map associating each user ID with the list of adjacent friend IDs
     */
    private Map<Long, List<Long>> buildAdjacencyList() {
        Map<Long, List<Long>> adjList = toStream(userRepo.getAll())
                .collect(Collectors.toMap(User::getId, u -> new ArrayList<>()));

        toStream(friendshipRepo.getAll())
                .forEach(friendship -> {
                    Long u1 = friendship.getLeftId();
                    Long u2 = friendship.getRightId();

                    if (adjList.containsKey(u1) && adjList.containsKey(u2)) {
                        adjList.get(u1).add(u2);
                        adjList.get(u2).add(u1);
                    }
                });

        return adjList;
    }

    /**
     * Collects all nodes belonging to a connected component via depth-first search (DFS).
     *
     * @param current   the current node being traversed
     * @param adjList   the graph adjacency list
     * @param visited   the set of visited node IDs to avoid cycles
     * @param component the collection accumulating nodes of the current connected component
     */
    private void dfsCollectComponent(Long current, Map<Long, List<Long>> adjList, Set<Long> visited, List<Long> component) {
        visited.add(current);
        component.add(current);

        adjList.getOrDefault(current, Collections.emptyList()).stream()
                .filter(neighbor -> !visited.contains(neighbor))
                .forEach(neighbor -> dfsCollectComponent(neighbor, adjList, visited, component));
    }

    /**
     * Finds all connected components (communities) in the social network graph.
     *
     * @return a list of components, where each component is represented as a list of user IDs
     */
    private List<List<Long>> getAllComponents() {
        Map<Long, List<Long>> adjList = buildAdjacencyList();
        Set<Long> visited = new HashSet<>();
        List<List<Long>> components = new ArrayList<>();

        adjList.keySet().stream()
                .filter(userId -> !visited.contains(userId))
                .forEach(userId -> {
                    List<Long> component = new ArrayList<>();
                    dfsCollectComponent(userId, adjList, visited, component);
                    components.add(component);
                });

        return components;
    }

    /**
     * Returns the total count of connected communities in the network.
     *
     * @return the total number of connected components
     */
    public int getNumberOfCommunities() {
        return getAllComponents().size();
    }

    /**
     * Recursive backtracking traversal to compute the longest simple path originating from a given node.
     *
     * @param current       the current node being evaluated along the path
     * @param adjList       the graph adjacency list
     * @param visitedOnPath the set tracking nodes currently included in the path to avoid cycles
     * @return the maximum length of a simple path starting from the current node
     */
    private int dfsLongestPathFromNode(Long current, Map<Long, List<Long>> adjList, Set<Long> visitedOnPath) {
        visitedOnPath.add(current);

        int maxLen = adjList.getOrDefault(current, Collections.emptyList()).stream()
                .filter(neighbor -> !visitedOnPath.contains(neighbor))
                .mapToInt(neighbor -> 1 + dfsLongestPathFromNode(neighbor, adjList, visitedOnPath))
                .max()
                .orElse(0);

        visitedOnPath.remove(current);
        return maxLen;
    }

    /**
     * Computes the longest simple path length within a given connected component using Stream operations.
     *
     * @param component the list of node IDs belonging to the connected component
     * @param adjList   the graph adjacency list
     * @return the length of the longest simple path found within the component
     */
    private int computeLongestPathInComponent(List<Long> component, Map<Long, List<Long>> adjList) {
        return component.stream()
                .mapToInt(node -> dfsLongestPathFromNode(node, adjList, new HashSet<>()))
                .max()
                .orElse(0);
    }

    /**
     * Determines the most sociable community, identified as the component having the longest simple path.
     *
     * @return a list of User entities forming the most sociable community, or an empty list if no communities exist
     */
    public List<User> getMostSociableCommunity() {
        List<List<Long>> components = getAllComponents();
        if (components.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<Long>> adjList = buildAdjacencyList();

        return components.stream()
                .max(Comparator.comparingInt(comp -> computeLongestPathInComponent(comp, adjList)))
                .map(bestComponent -> bestComponent.stream()
                        .map(userRepo::find)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Retrieves all users satisfying a specified predicate condition.
     *
     * @param filter the predicate used to evaluate and filter users
     * @return a list of users matching the filter predicate
     */
    public List<User> findUsersMatching(Predicate<User> filter) {
        return userRepo.findAll(filter);
    }
}