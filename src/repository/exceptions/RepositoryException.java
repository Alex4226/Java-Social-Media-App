package repository.exceptions;

/**
 * Custom unchecked exception thrown to signal data persistence errors.
 * Supports exception chaining to wrap underlying lower-level exceptions.
 */
public class RepositoryException extends RuntimeException {

    /**
     * Constructs a new RepositoryException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the error
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new RepositoryException with the specified detail message and cause.
     *
     * @param message the detail message explaining the cause of the error
     * @param cause   the underlying cause of the exception (e.g., IOException, ParseException)
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new RepositoryException with the specified cause.
     *
     * @param cause the underlying cause of the exception
     */
    public RepositoryException(Throwable cause) {
        super(cause);
    }
}