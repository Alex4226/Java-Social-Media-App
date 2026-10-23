package domain;

import java.util.Objects;

/**
 * Clasa generica de baza ce defineste o entitate identificabila in mod unic printr-un identificator.
 *
 * @param <ID> tipul identificatorului unic asociat entitatii
 */
public class Entity<ID> {
    private ID id;

    /**
     * Constructor implicit fara parametri.
     */
    public Entity() {}

    /**
     * Constructor care initializeaza entitatea cu identificatorul specificat.
     *
     * @param id identificatorul unic al entitatii
     */
    public Entity(ID id) {
        this.id = id;
    }

    /**
     * Returneaza identificatorul entitatii.
     *
     * @return identificatorul de tip {@code ID}
     */
    public ID getId() {
        return this.id;
    }

    /**
     * Seteaza sau actualizeaza identificatorul entitatii.
     *
     * @param id noul identificator ce va fi atribuit entitatii
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Verifica daca entitatea curenta este egala cu un alt obiect.
     * Doua entitati sunt considerate egale daca apartin aceleiasi clase si au acelasi identificator.
     *
     * @param o obiectul cu care se compara instanta curenta
     * @return {@code true} daca obiectele sunt egale, altfel {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        return Objects.equals(this.id, ((Entity<?>) o).id);
    }

    /**
     * Calculeaza si returneaza valoarea hash a entitatii pe baza identificatorului acesteia.
     *
     * @return codul hash asociat identificatorului
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}