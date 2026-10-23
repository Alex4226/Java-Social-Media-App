package domain;

import java.util.Objects;

/**
 * Clasa generica ce defineste un tuplu format din doua elemente.
 *
 * @param <E1> tipul primului element din tuplu
 * @param <E2> tipul celui de-al doilea element din tuplu
 */
public class Tuple<E1, E2> {
    private E1 left;
    private E2 right;

    /**
     * Constructor care initializeaza tuplul cu cele doua valori specificate.
     *
     * @param left  primul element al tuplului
     * @param right al doilea element al tuplului
     */
    public Tuple(E1 left, E2 right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Returneaza primul element al tuplului.
     *
     * @return elementul stang de tip {@code E1}
     */
    public E1 getLeft() {
        return this.left;
    }

    /**
     * Returneaza al doilea element al tuplului.
     *
     * @return elementul drept de tip {@code E2}
     */
    public E2 getRight() {
        return this.right;
    }

    /**
     * Actualizeaza primul element al tuplului.
     *
     * @param left noua valoare pentru elementul stang
     */
    public void setLeft(E1 left) {
        this.left = left;
    }

    /**
     * Actualizeaza al doilea element al tuplului.
     *
     * @param right noua valoare pentru elementul drept
     */
    public void setRight(E2 right) {
        this.right = right;
    }

    /**
     * Verifica egalitatea dintre tuplul curent si un alt obiect.
     * Doua tupluri sunt egale daca elementele lor corespunzatoare sunt egale.
     *
     * @param o obiectul cu care se compara instanta curenta
     * @return {@code true} daca obiectele sunt egale, altfel {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        return Objects.equals(this.left, ((Tuple<?, ?>) o).left) && Objects.equals(this.right, ((Tuple<?, ?>) o).right);
    }

    /**
     * Calculeaza codul hash pe baza celor doua elemente componente.
     *
     * @return valoarea hash asociata tuplului
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.left, this.right);
    }

    /**
     * Returneaza o reprezentare textuala a tuplului.
     *
     * @return sirul de caractere continand cele doua elemente intre paranteze
     */
    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}