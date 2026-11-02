package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a user in the social network application.
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String email;
    private final List<User> friends = new ArrayList<>();

    /**
     * Constructs a new User instance.
     *
     * @param id        the unique identifier of the user
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email address
     */
    public User(Long id, String firstName, String lastName, String email) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Retrieves the first name of the user.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Updates the first name of the user.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retrieves the last name of the user.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Updates the last name of the user.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Retrieves the email address of the user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the email address of the user.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the list of friends connected to this user.
     *
     * @return the list of friend entities
     */
    public List<User> getFriends() {
        return friends;
    }

    /**
     * Adds an existing user as a friend if not already present.
     *
     * @param friend the user to add as friend
     */
    public void addFriend(User friend) {
        if (friend != null && !this.friends.contains(friend)) {
            this.friends.add(friend);
        }
    }

    /**
     * Removes a user from the list of friends.
     *
     * @param friend the user to remove from friends
     */
    public void removeFriend(User friend) {
        if (friend != null) {
            this.friends.remove(friend);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", friendsCount=" + friends.size() +
                '}';
    }
}