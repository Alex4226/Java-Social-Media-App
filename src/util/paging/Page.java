package util.paging;

/**
 * Represents a single page of results along with the total number of elements available.
 *
 * @param <E> the type of elements on the page
 */
public class Page<E> {
    private final Iterable<E> elementsOnPage;
    private final int totalNumberOfElements;

    /**
     * Constructs a new Page instance.
     *
     * @param elementsOnPage        the elements contained on this page
     * @param totalNumberOfElements the total number of elements across all pages
     */
    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    /**
     * Returns the elements contained on the current page.
     *
     * @return an {@link Iterable} of elements on this page
     */
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    /**
     * Returns the total number of elements across all pages.
     *
     * @return the total number of elements
     */
    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}