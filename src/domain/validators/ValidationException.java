package domain.validators;

/**
 * Exceptie specifica aruncata atunci cand datele unei entitati nu respecta regulile de validare[cite: 3].
 */
public class ValidationException extends RuntimeException {
    /**
     * Constructor care primeste mesajul descriptiv al erorii de validare.
     *
     * @param message mesajul asociat erorii
     */
    public ValidationException(String message) {
        super(message);
    }
}