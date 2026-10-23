package domain.validators;

/**
 * Interfata generica pentru validarea entitatilor din aplicatie (Strategy Pattern)[cite: 3].
 *
 * @param <T> tipul entitatii ce urmeaza sa fie validata
 */
public interface Validator<T> {
    /**
     * Valideaza o entitate conform regulilor de consistenta a datelor[cite: 3].
     *
     * @param entity entitatea care se valideaza
     * @throws ValidationException daca datele entitatii nu sunt valide[cite: 3]
     */
    void validate(T entity) throws ValidationException;
}