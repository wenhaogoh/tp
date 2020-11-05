package seedu.address.model.history;

/**
 * Represents a node in a doubly-linked list.
 * @param <T> the type of the doubly-linked list.
 */
public class Node<T> {
    private T value;
    private Node<T> next;
    private Node<T> previous;

    /**
     * Constructs a {@code Node}
     */
    public Node(T value) {
        this.value = value;
        this.next = null;
        this.previous = null;
    }

    public T getValue() {
        return value;
    }

    /**
     * Creates a bi-directional forward connection to another {@code Node}.
     * @param toAdd the {@code Node} to be added.
     */
    public void add(Node<T> toAdd) {
        toAdd.setPrevious(this);
        setNext(toAdd);
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> getPrevious() {
        return previous;
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasPrevious() {
        return previous != null;
    }

    private void setNext(Node<T> next) {
        this.next = next;
    }

    private void setPrevious(Node<T> previous) {
        this.previous = previous;
    }
}
