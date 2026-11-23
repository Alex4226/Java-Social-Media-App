package repository;

import domain.Entity;
import util.paging.Page;
import util.paging.Pageable;

/**
 * Repository interface supporting paginated retrieval of entities.
 * @param <ID> the entity identifier type
 * @param <E> the entity type extending {@code Entity<ID>}
 */
public interface PagingRepository<ID, E extends  Entity<ID>> extends  Repository<ID, E> {

    /**
     * Retrieves a specific page of entities
     * @param pageable pagination parameters specifying page number and page size
     * @return a {@link Page} containing the elements on the requested page and the total element count
     */
    Page<E> findAllOnPage(Pageable pageable);
}
