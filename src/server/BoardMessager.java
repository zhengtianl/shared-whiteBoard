/**
 * Class for managing the white board.
 * RMI methods are implemented here for clients to call remotely.
 */

package server;

import client.InterFaceClient;
import client.ClientMessager;
import canvas.InterFaceCanvasMsg;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

public class BoardMessager extends UnicastRemoteObject implements InterFaceBoardMgr, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ClientMessager manager;

    private InterFaceClient clientManager;

    public BoardMessager() throws RemoteException {
        manager = new ClientMessager();
    }

    @Override
    public void login(InterFaceClient client) throws RemoteException {
        // The first client is the manager
        if (this.manager.hasNoClient()) {
            client.setAsManager();
            client.setUsername("(Host) " + client.getUsername());
            this.clientManager = client;
            this.manager.addClient(client);
            syncClientList();
            try {
                syncChatHistory(client);
            } catch (IOException e) {
                System.out.println("Error syncing chat history!");
            }
            return;
        }

        // Other clients need to be approved by the manager to join in
        boolean access = true;
        try {
            access = client.needAccess(client.getUsername());
        } catch (Exception e) {
            System.out.println("Unable to get access to the canvas!");
        }

        if (access) {
            this.manager.addClient(client);
            syncClientList();
            try {
                syncChatHistory(client);
            } catch (IOException e) {
                System.out.println("Error syncing chat history!");
            }
        } else {
            try {
                client.setAccess(false);
            } catch (Exception e) {
                System.out.println("Unable to set access!");
            }
        }
    }

    @Override
    public boolean invalidUsername(String username) throws RemoteException {
        boolean res = false;
        for (InterFaceClient c : getClients()) {
            if (username.equals(c.getUsername()) || c.getUsername().equals("(Host) " + username)) {
                return true;
            }
        }
        return res;
    }

    @Override
    public Set<InterFaceClient> getClients() throws RemoteException {
        return this.manager.getClientList();
    }

    @Override
    public void syncClientList() throws RemoteException {
        for (InterFaceClient c: this.manager.getClientList()) {
            c.syncClientList(this.manager.getClientList());
        }
    }

    @Override
    public void quitClient(String username) throws RemoteException {
        for (InterFaceClient c: this.manager.getClientList()) {
            if (c.getUsername().equals(username)) {
                this.manager.delClient(c);
                syncClientList();
                System.out.println(username + " has left");
                return;
            }
        }
    }

    @Override
    public void kickClient(String username) throws RemoteException {
        for (InterFaceClient c: this.manager.getClientList()) {
            if (c.getUsername().equals(username)) {
                try {
                    c.forceQuit();
                } catch (IOException e) {
                    System.out.println("Cannot force quit!");
                }
                this.manager.delClient(c);
                syncClientList();
                System.out.println(username + " has been kicked out");
                return;
            }
        }
    }

    @Override
    public void removeAllClients() throws IOException {
        for (InterFaceClient c: this.manager.getClientList()) {
            this.manager.delClient(c);
            c.forceQuit();
        }
        System.out.println("Manager has end the session");
    }

    @Override
    public void broadcastMsg(InterFaceCanvasMsg draw) throws RemoteException {
        for (InterFaceClient c: this.manager.getClientList()) {
            c.syncCanvas(draw);
        }
    }

    @Override
    public byte[] sendCurrentCanvas() throws IOException {
        return this.clientManager.getCurrentCanvas();
    }

    @Override
    public void sendExistCanvas(byte[] canvas) throws IOException {
        for (InterFaceClient c: this.manager.getClientList()) {
            c.overrideCanvas(canvas);
        }
    }

    @Override
    public void cleanCanvas() throws RemoteException {
        for (InterFaceClient c: this.manager.getClientList()) {
            c.cleanCanvas();
        }
    }

    @Override
    public void broadcastChat(String msg) throws RemoteException {
        for (InterFaceClient c: this.manager.getClientList()) {
            c.syncChat(msg);
        }
    }

    @Override
    public void syncChatHistory(InterFaceClient client) throws IOException {
        client.syncChatHistory(this.clientManager.getChatHistory());
    }

}