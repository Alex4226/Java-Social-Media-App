package domain;

/**
 * Reprezinta un utilizator din cadrul retelei sociale[cite: 1, 2].
 * Extinde clasa generica Entity, avand identificatorul de tip Long[cite: 1].
 */
public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String email;

    /**
     * Constructor care initializeaza un utilizator cu ID, prenume, nume si adresa de email[cite: 1].
     *
     * @param id        identificatorul unic al utilizatorului[cite: 1]
     * @param firstName prenumele utilizatorului
     * @param lastName  numele de familie al utilizatorului
     * @param email     adresa de email a utilizatorului
     */
    public User(Long id, String firstName, String lastName, String email) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Returneaza prenumele utilizatorului.
     *
     * @return prenumele utilizatorului
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Returneaza numele de familie al utilizatorului.
     *
     * @return numele de familie
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Returneaza adresa de email a utilizatorului.
     *
     * @return adresa de email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Actualizeaza prenumele utilizatorului.
     *
     * @param firstName noul prenume ce va fi setat
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Actualizeaza numele de familie al utilizatorului.
     *
     * @param lastName noul nume de familie ce va fi setat
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Actualizeaza adresa de email a utilizatorului.
     *
     * @param email noua adresa de email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returneaza o reprezentare textuala a atributelor utilizatorului.
     *
     * @return sirul de caractere continand ID-ul, numele, prenumele si email-ul
     */
    @Override
    public String toString() {
        return "ID: " + this.getId() + " , Nume: " + this.getLastName() + " , Prenume: " + this.getFirstName() + " , Email: " + this.getEmail();
    }
}