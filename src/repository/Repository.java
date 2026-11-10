package repository;

import domain.Entity;
import domain.validators.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

/**
 * Generic CRUD repository interface using Java 8 Optional and Stream API.
 *
 * @param <ID> the entity identifier type
 * @param <E>  the entity type extending Entity<ID>
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the identifier of the entity, must not be null
     * @return an {@link Optional} containing the entity if found, or empty if not
     * @throws IllegalArgumentException if the provided id is null
     */
    Optional<E> find(ID id);

    /**
     * Retrieves all entities stored in the repository.
     *
     * @return an iterable collection of all entities
     */
    Iterable<E> getAll();

    /**
     * Saves an entity in the repository.
     *
     * @param entity the entity to save, must not be null
     * @return an empty {@link Optional} if saved successfully, or an {@link Optional} containing the existing entity
     * @throws ValidationException      if the entity fails validation
     * @throws IllegalArgumentException if the entity is null
     */
    Optional<E> add(E entity);

    /**
     * Removes an entity by its identifier.
     *
     * @param id the identifier of the entity to remove, must not be null
     * @return an {@link Optional} containing the removed entity, or empty if no entity had that id
     * @throws IllegalArgumentException if the provided id is null
     */
    Optional<E> delete(ID id);

    /**
     * Updates an existing entity.
     *
     * @param entity the entity with updated values, must not be null
     * @return an empty {@link Optional} if updated, or an {@link Optional} containing the entity if it does not exist
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the entity is null
     */
    Optional<E> update(E entity);

    /**
     * Retrieves all entities matching a given predicate condition.
     *
     * @param filter the predicate used to evaluate entities
     * @return a list of entities satisfying the predicate
     */
    default List<E> findAll(Predicate<E> filter) {
        return StreamSupport.stream(getAll().spliterator(), false)
                .filter(filter)
                .toList();
    }
}