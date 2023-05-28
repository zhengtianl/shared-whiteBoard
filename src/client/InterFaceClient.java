/**
 * Interface for a client.
 */

package client;

import canvas.InterFaceCanvasMsg;

import javax.swing.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface InterFaceClient extends Remote {

    /**
     * Retrieves the client's username.
     *
     * @return The username of the client.
     * @throws RemoteException If the remote operation fails.
     */
    String getUsername() throws RemoteException;

    /**
     * Updates the client's username.
     *
     * @param s The new username.
     * @throws RemoteException If the remote operation fails.
     */
    void setUsername(String s) throws RemoteException;

    /**
     * Sets the client as a manager.
     *
     * @throws RemoteException If the remote operation fails.
     */
    void setAsManager() throws RemoteException;

    /**
     * Requests access to the canvas.
     *
     * @param username The username of the client requesting access.
     * @return True if access is granted, false otherwise.
     * @throws RemoteException If the remote operation fails.
     */
    boolean needAccess(String username) throws RemoteException;

    /**
     * Retrieves the client's access status.
     *
     * @return True if the client has access, false otherwise.
     * @throws RemoteException If the remote operation fails.
     */
    boolean getAccess() throws RemoteException;

    /**
     * Updates the client's access status.
     *
     * @param access The new access status.
     * @throws RemoteException If the remote operation fails.
     */
    void setAccess(boolean access) throws RemoteException;

    /**
     * Updates the list of connected clients.
     *
     * @param clientList The new list of clients.
     * @throws RemoteException If the remote operation fails.
     */
    void syncClientList(Set<InterFaceClient> clientList) throws RemoteException;

    /**
     * Synchronizes the latest updates on the canvas.
     *
     * @param draw The new drawing on the canvas.
     * @throws RemoteException If the remote operation fails.
     */
    void syncCanvas(InterFaceCanvasMsg draw) throws RemoteException;

    /**
     * Clears the canvas.
     *
     * @throws RemoteException If the remote operation fails.
     */
    void cleanCanvas() throws RemoteException;

    /**
     * Retrieves the current state of the canvas.
     *
     * @return The current state of the canvas as a byte array.
     * @throws IOException If an I/O error occurs.
     */
    byte[] getCurrentCanvas() throws IOException;

    /**
     * Replaces the current canvas with a new one.
     *
     * @param canvas The new canvas as a byte array.
     * @throws IOException If an I/O error occurs.
     */
    void overrideCanvas(byte[] canvas) throws IOException;

    /**
     * Forces the client to exit the whiteboard.
     *
     * @throws IOException If an I/O error occurs.
     */
    void forceQuit() throws IOException;

    /**
     * Synchronizes a new chat message.
     *
     * @param msg The new chat message.
     * @throws RemoteException If the remote operation fails.
     */
    void syncChat(String msg) throws RemoteException;

    /**
     * Retrieves the current chat history.
     *
     * @return The current chat history.
     * @throws IOException If an I/O error occurs.
     */
    DefaultListModel<String> getChatHistory() throws IOException;

    /**
     * Synchronizes the chat history for clients that have just joined.
     *
     * @param chatHistory The chat history to synchronize.
     * @throws RemoteException If the remote operation fails.
     */
    void syncChatHistory(DefaultListModel<String> chatHistory) throws RemoteException;

    /**
     * Configures the user interface elements like buttons and windows.
     *
     * @throws RemoteException If the remote operation fails.
     */
    void configUI() throws RemoteException;

    /**
     * Initializes and renders the user interface.
     *
     * @throws RemoteException If the remote operation fails.
     */
    void renderUI() throws RemoteException;
}
