package client;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class that manages a list of clients.
 */
public class ClientMessager {

    // The list of connected clients.
    private final Set<InterFaceClient> clientList;

    /**
     * Constructor for the ClientMessager class.
     */
    public ClientMessager() {
        // Use a CopyOnWriteArraySet to allow concurrent access.
        this.clientList = new CopyOnWriteArraySet<>();
    }

    /**
     * Retrieves an unmodifiable view of the list of clients.
     *
     * @return An unmodifiable set of clients.
     */
    public Set<InterFaceClient> getClientList() {
        return Collections.unmodifiableSet(this.clientList);
    }

    /**
     * Adds a client to the list of clients.
     *
     * @param client The client to add.
     */
    public void addClient(InterFaceClient client) {
        this.clientList.add(client);
    }

    /**
     * Removes a client from the list of clients.
     *
     * @param client The client to remove.
     */
    public void delClient(InterFaceClient client) {
        this.clientList.remove(client);
    }

    /**
     * Checks if the list of clients is empty.
     *
     * @return True if the list of clients is empty, false otherwise.
     */
    public boolean hasNoClient() {
        return this.clientList.isEmpty();
    }
}
