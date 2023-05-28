/**
 * Run the first client to create a board.
 * This client will be the manager.
 */

package client;

import server.InterFaceBoardMgr;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class Creator {

    public static void main(String[] args) {

        // Default IP address and port for server
        String serverIP = "localhost";
        String serverPort = "3200";
        // Default username for the first user
        String username = "Hoster";

        // Specify IP address and port from arguments
        if (args.length > 0) {
            if (args.length != 3) {
                System.out.println("Invalid arguments. Expected <serverIP> <serverPort> <username>");
                System.exit(0);
            } else {
                serverIP = args[0];
                serverPort = args[1];
                username = args[2];
            }
        }

        try {
            // Locate the server
            String serverAddress = "//" + serverIP + ":"+ serverPort + "/Canvas";
            InterFaceBoardMgr server = (InterFaceBoardMgr) Naming.lookup(serverAddress);

            // Create a new client and try to login to the server
            InterFaceClient client = new Client(server, username);
            try {
                server.login(client);
            } catch(RemoteException e) {
                System.err.println("Login error: Unable to connect to server. Please check your network connection and server status.");
                System.exit(0);
            }

            // Render the client's UI
            client.renderUI();
        } catch(Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(0);
        }
    }
}
