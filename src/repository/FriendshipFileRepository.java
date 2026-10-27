package repository;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Class for friendship file repository that extends abstract file repository
 */
public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    /**
     * Constructor for friendship file repository
     * @param validator used validator for validation of friendships
     * @param filename the filename for the file that saves the friendships
     */
    public FriendshipFileRepository(Validator<Friendship> validator, String filename) {
        super(validator, filename);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        if (attributes.size() < 3) {
            throw new IllegalArgumentException("Invalid line format: expected 3 fields");
        }

        Long id1 = Long.parseLong(attributes.get(0).trim());
        Long id2 = Long.parseLong(attributes.get(1).trim());
        LocalDateTime date = LocalDateTime.parse(attributes.get(2).trim());

        Friendship friendship = new Friendship(new Tuple<>(min(id1, id2), max(id1, id2)), date);
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship friendship) {
        Long id1 = friendship.getLeftId();
        Long id2 = friendship.getRightId();
        LocalDateTime date = friendship.getDate();

        return id1 + ";" + id2 + ";" + date;
    }
}
