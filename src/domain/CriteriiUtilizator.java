package domain;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utility class providing reusable filtering predicates for {@link User} entities.
 */
public final class CriteriiUtilizator {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private CriteriiUtilizator() {
    }

    /**
     * Builds a predicate checking if a user's last name starts with the given prefix (case-insensitive).
     *
     * @param prefix the prefix string to match against the user's last name
     * @return a predicate testing the last name prefix
     * @throws NullPointerException if prefix is null
     */
    public static Predicate<User> byLastNamePrefix(String prefix) {
        Objects.requireNonNull(prefix, "Prefix must not be null");
        return user -> user != null
                && user.getLastName() != null
                && user.getLastName().toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * Builds a predicate checking if a user's email ends with the specified domain.
     *
     * @param domain the domain string to match (e.g., "scs.ubbcluj.ro")
     * @return a predicate testing the email domain
     * @throws NullPointerException if domain is null
     */
    public static Predicate<User> byEmailDomain(String domain) {
        Objects.requireNonNull(domain, "Domain must not be null");
        return user -> user != null
                && user.getEmail() != null
                && user.getEmail().toLowerCase().endsWith(domain.toLowerCase());
    }

    /**
     * Evaluates whether a user has no friends registered in the network.
     * Designed to be referenced via static method reference: {@code CriteriiUtilizator::faraPrieteni}.
     *
     * @param user the user to evaluate
     * @return {@code true} if the user has no friends or friends list is empty, {@code false} otherwise
     */
    public static boolean faraPrieteni(User user) {
        return user != null && (user.getFriends() == null || user.getFriends().isEmpty());
    }

    /**
     * Builds a predicate checking if a user has at least one friend.
     * Demonstrates predicate negation using {@link Predicate#not(Predicate)}.
     *
     * @return a predicate verifying if a user has registered friends
     */
    public static Predicate<User> cuPrieteni() {
        return Predicate.not(CriteriiUtilizator::faraPrieteni);
    }
}