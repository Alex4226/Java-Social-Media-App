package repository;

import domain.Entity;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Generic interface for CRUD (Create, Read, Update, Delete) operations on domain entities.
 *
 * @param <ID> the type of the entity unique identifier
 * @param <E>  the type of the entity extending {@link Entity} of ID
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the unique identifier of the entity to find
     * @return the found entity, or {@code null} if no entity exists with the specified id
     */
    E find(ID id);

    /**
     * Retrieves all stored entities.
     *
     * @return an {@link Iterable} containing all entities
     */
    Iterable<E> getAll();

    /**
     * Adds an entity to the repository.
     *
     * @param entity the entity to be added
     * @return {@code null} if the entity was added successfully, or the existing entity if an ID conflict occurs
     */
    E add(E entity);

    /**
     * Deletes an entity from the repository based on its unique identifier.
     *
     * @param id the unique identifier of the entity to be deleted
     * @return the removed entity, or {@code null} if no entity was found with the specified id
     */
    E delete(ID id);

    /**
     * Updates an existing entity in the repository.
     *
     * @param entity the new state of the entity
     * @return {@code null} if the entity was updated successfully, or the provided entity if not found
     */
    E update(E entity);

    /**
     * Retrieves all entities that satisfy the provided filtering predicate.
     *
     * @param filter the predicate used to filter entities
     * @return a {@link List} of entities matching the criteria
     * @throws NullPointerException if the filter predicate is null
     */
    default List<E> findAll(Predicate<E> filter) {
        Objects.requireNonNull(filter, "Filter predicate must not be null");
        return StreamSupport.stream(getAll().spliterator(), false)
                .filter(filter)
                .collect(Collectors.toList());
    }
}