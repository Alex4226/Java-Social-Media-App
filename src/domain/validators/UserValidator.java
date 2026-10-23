package domain.validators;

import domain.User;

/**
 * Validator concret pentru entitatea User[cite: 1, 3].
 * Implementeaza interfata Validator conform sablonului de proiectare Strategy[cite: 3].
 */
public class UserValidator implements Validator<User> {

    /**
     * Valideaza datele unui utilizator[cite: 1, 3].
     *
     * @param entity utilizatorul care se valideaza[cite: 1, 3]
     * @throws ValidationException daca utilizatorul este null, daca numele sau prenumele sunt goale,
     *                             sau daca adresa de email nu contine caracterul '@'[cite: 3]
     */
    @Override
    public void validate(User entity) {
        if (entity == null) {
            throw new ValidationException("User cannot be null!");
        }

        if (entity.getFirstName() == null || entity.getFirstName().trim().isEmpty()) {
            throw new ValidationException("User needs to have a first name!");
        }

        if (entity.getLastName() == null || entity.getLastName().trim().isEmpty()) {
            throw new ValidationException("User needs to have a last name!");
        }

        if (entity.getEmail() == null || !entity.getEmail().contains("@")) {
            throw new ValidationException("User has invalid email (needs to have an @)");
        }
    }
}