package util.paging;

/**
 * Represents a pagination request containing the page number and page size
 */
public class Pageable {
    private final int pageNumber;
    private final int pageSize;

    /**
     * Constructs a new Pageable instance
     * @param pageNumber the 0 based page index (ge 0)
     * @param pageSize the maximum number of elements per page (gt 0)
     * @throws IllegalArgumentException if pageNumber lt 0 or pageSize le 0
     */
    public Pageable(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be negative!");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0!");
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }
}
