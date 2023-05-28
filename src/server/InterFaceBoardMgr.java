package server;

import client.InterFaceClient;
import canvas.InterFaceCanvasMsg;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Interface for managing the white board.
 * Methods below can be called by clients remotely.
 */
public interface InterFaceBoardMgr extends Remote {

    // Records a client connected to the server
    void login(InterFaceClient client) throws RemoteException;

    // Checks if the username is duplicated
    boolean invalidUsername(String username) throws RemoteException;

    // Retrieves a list of clients
    Set<InterFaceClient> getClients() throws RemoteException;

    // Syncs client lists across all clients
    void syncClientList() throws RemoteException;

    // Handles a client quitting the whiteboard
    void quitClient(String username) throws RemoteException;

    // Allows the manager to kick out a client
    void kickClient(String username) throws RemoteException;

    // Removes all the clients
    void removeAllClients() throws IOException;

    // Broadcasts updates of canvas to all clients
    void broadcastMsg(InterFaceCanvasMsg draw) throws RemoteException;

    // Sends the current canvas to newly joined clients
    byte[] sendCurrentCanvas() throws IOException;

    // Sends existing canvas to all clients when the manager opens it
    void sendExistCanvas(byte[] canvas) throws IOException;

    // Cleans the shared canvas
    void cleanCanvas() throws RemoteException;

    // Sends the new chat to the chat window
    void broadcastChat(String chat) throws RemoteException;

    // Sends the current chat history to newly joined clients
    void syncChatHistory(InterFaceClient client) throws IOException;
}
