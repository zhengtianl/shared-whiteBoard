/**
 * Run the client to join a board.
 */

package client;

import server.InterFaceBoardMgr;

import javax.swing.*;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class User {

    public static void main(String[] args) {

        // Default IP address and port for server
        String serverIP = "localhost";
        String serverPort = "3200";
        // Default username for users
        String username = "host1";

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

            // Validate the username
            if (server.invalidUsername(username)) {
                System.out.println("The name has been taken! Please choose another.");
                System.exit(0);
            }
            
            // Create a new client
            InterFaceClient client = new Client(server, username);
            
            // Attempt to login
            try {
                server.login(client);
            } catch(RemoteException e) {
                System.err.println("Login error: Unable to connect to server. Please check your network connection and server status.");
                System.exit(0);
            }

            // Check client's access
            if (client.getAccess()) {
                // Render the UI for the client
                client.renderUI();
            } else {
                // If client does not have access, display a message and quit the client
                server.quitClient(username);
                JOptionPane.showMessageDialog(null,
                        "Access denied! Please check with the administrator.",
                        "Warning", JOptionPane.INFORMATION_MESSAGE);
                client.forceQuit();
            }

        } catch(Exception e) {
        	System.err.println("Unexpected error: " + e.getMessage());
            System.exit(0);
        }
    }
}
