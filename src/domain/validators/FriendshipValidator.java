package domain.validators;

import domain.Friendship;

/**
 * Validator concret pentru entitatea Friendship[cite: 1, 3].
 * Implementeaza interfata Validator conform sablonului de proiectare Strategy[cite: 3].
 */
public class FriendshipValidator implements Validator<Friendship> {

    /**
     * Valideaza o relatie de prietenie intre doi utilizatori[cite: 1, 2, 3].
     *
     * @param entity entitatea de tip Friendship care se valideaza[cite: 1, 3]
     * @throws ValidationException daca relatia este null, daca unul dintre ID-uri este null,
     *                             sau daca un utilizator incearca sa fie prieten cu el insusi[cite: 3]
     */
    @Override
    public void validate(Friendship entity) {
        if (entity == null) {
            throw new ValidationException("Friendship cannot be null!");
        }

        if (entity.getLeftId() == null || entity.getRightId() == null) {
            throw new ValidationException("One of the ids is invalid!");
        }

        if (entity.getLeftId().equals(entity.getRightId())) {
            throw new ValidationException("An user cannot be friends with themselves!");
        }
    }
}