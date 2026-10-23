package domain;

import java.time.LocalDateTime;

/**
 * Reprezinta o relatie de prietenie intre doi utilizatori din cadrul retelei sociale[cite: 1, 2].
 * Identificatorul entitatii este format dintr-un tuplu de doua ID-uri de utilizator[cite: 2].
 */
public class Friendship extends Entity<Tuple<Long, Long>> {

    private LocalDateTime date;

    /**
     * Constructor care initializeaza o relatie de prietenie cu un identificator si o data specificata[cite: 2].
     *
     * @param id   identificatorul unic al prieteniei, reprezentat de perechea de ID-uri de utilizatori[cite: 2]
     * @param date data si ora la care s-a legat relatia de prietenie
     */
    public Friendship(Tuple<Long, Long> id, LocalDateTime date) {
        super(id);
        this.date = date;
    }

    /**
     * Constructor care initializeaza o relatie de prietenie cu data si ora curenta[cite: 2].
     *
     * @param id identificatorul unic al prieteniei, reprezentat de perechea de ID-uri de utilizatori[cite: 2]
     */
    public Friendship(Tuple<Long, Long> id) {
        super(id);
        this.date = LocalDateTime.now();
    }

    /**
     * Returneaza data la care a fost creata relatia de prietenie.
     *
     * @return data si ora crearii relatiei
     */
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * Actualizeaza data relatiei de prietenie.
     *
     * @param date noua data asociata prieteniei
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Returneaza o reprezentare sub forma de sir de caractere a prieteniei.
     *
     * @return reprezentarea textuala a entitatii Friendship
     */
    @Override
    public String toString() {
        return "ID: " + getId() + ", Date: " + this.date;
    }

    /**
     * Returneaza identificatorul primului utilizator din cadrul relatiei de prietenie.
     *
     * @return ID-ul primului utilizator
     */
    public Long getLeftId() {
        return this.getId().getLeft();
    }

    /**
     * Returneaza identificatorul celui de-al doilea utilizator din cadrul relatiei de prietenie.
     *
     * @return ID-ul celui de-al doilea utilizator
     */
    public Long getRightId() {
        return this.getId().getRight();
    }
}