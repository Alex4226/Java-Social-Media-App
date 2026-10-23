package repository;

import domain.Entity;

/**
 * Interfata generica pentru operatii de tip CRUD (Create, Read, Update, Delete) asupra entitatilor din aplicatie[cite: 1, 3].
 *
 * @param <ID> tipul identificatorului unic al entitatii
 * @param <E>  tipul entitatii stocate, care extinde Entity de ID
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * Cauta o entitate pe baza identificatorului unic primit.
     *
     * @param id identificatorul entitatii cautate
     * @return entitatea gasita sau {@code null} daca nu exista nicio entitate cu acest identificator
     */
    E find(ID id);

    /**
     * Returneaza toate entitatile stocate in repository.
     *
     * @return o colectie iterabila ce contine toate entitatile
     */
    Iterable<E> getAll();

    /**
     * Adauga o entitate in repository[cite: 3].
     *
     * @param entity entitatea care urmeaza sa fie adaugata
     * @return {@code null} daca entitatea a fost adaugata cu succes, sau entitatea existenta in caz de conflict de ID
     */
    E add(E entity);

    /**
     * Sterge o entitate din repository pe baza identificatorului sau[cite: 3].
     *
     * @param id identificatorul entitatii ce trebuie stearsa
     * @return entitatea stearsa sau {@code null} daca nu a fost gasita nicio entitate cu acel ID
     */
    E delete(ID id);

    /**
     * Actualizeaza o entitate existenta in repository.
     *
     * @param entity noua stare a entitatii
     * @return {@code null} daca entitatea a fost actualizata cu succes, sau entitatea furnizata daca nu a fost gasita
     */
    E update(E entity);
}