package repository;

import domain.Entity;
import domain.validators.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementare in memorie a interfetei Repository, utilizand o colectie de tip Map[cite: 3].
 *
 * @param <ID> tipul identificatorului unic al entitatii
 * @param <E>  tipul entitatii stocate, care extinde Entity de ID
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private final Validator<E> validator;
    protected Map<ID, E> entities;

    /**
     * Constructor care initializeaza repository-ul in memorie cu validatorul specificat[cite: 3].
     *
     * @param validator validatorul utilizat pentru validarea entitatilor inainte de persistare[cite: 3]
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        this.entities = new HashMap<>();
    }

    /**
     * Cauta o entitate pe baza identificatorului primit.
     *
     * @param id identificatorul entitatii cautate
     * @return entitatea gasita sau {@code null} daca aceasta nu exista
     * @throws IllegalArgumentException daca identificatorul primit este null
     */
    @Override
    public E find(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null!");
        }
        return entities.get(id);
    }

    /**
     * Returneaza toate entitatile stocate in memorie.
     *
     * @return o colectie iterabila cu toate entitatile existente
     */
    @Override
    public Iterable<E> getAll() {
        return entities.values();
    }

    /**
     * Valideaza si adauga o noua entitate in colectia din memorie[cite: 3].
     *
     * @param entity entitatea care urmeaza sa fie adaugata
     * @return {@code null} daca adaugarea s-a realizat cu succes, sau entitatea existenta daca ID-ul se afla deja in memorie
     * @throws IllegalArgumentException daca entitatea este null
     * @throws domain.validators.ValidationException daca datele entitatii nu sunt valide[cite: 3]
     */
    @Override
    public E add(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null!");
        }

        validator.validate(entity);

        if (entities.containsKey(entity.getId())) {
            return entity;
        }

        entities.put(entity.getId(), entity);
        return null;
    }

    /**
     * Sterge o entitate din memorie pe baza identificatorului primit[cite: 3].
     *
     * @param id identificatorul entitatii care trebuie eliminata
     * @return entitatea eliminata sau {@code null} daca aceasta nu exista
     * @throws IllegalArgumentException daca identificatorul primit este null
     */
    @Override
    public E delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id is invalid!");
        }

        return entities.remove(id);
    }

    /**
     * Valideaza si actualizeaza o entitate existenta in colectia din memorie.
     *
     * @param entity entitatea actualizata
     * @return {@code null} daca actualizarea s-a realizat cu succes, sau entitatea primita daca nu a fost gasita in memorie
     * @throws IllegalArgumentException daca entitatea este null
     * @throws domain.validators.ValidationException daca datele entitatii nu sunt valide[cite: 3]
     */
    @Override
    public E update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }

        validator.validate(entity);

        if (entities.containsKey(entity.getId())) {
            entities.put(entity.getId(), entity);
            return null;
        }

        return entity;
    }
}