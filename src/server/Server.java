/**
 * This class represents the server for the application.
 */
package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.JOptionPane;

public class Server {

    /**
     * The main method to start the server.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // Default port number is 3200
        String port = "3200";

        // If there are command line arguments, use the first one as the port number
        if (args.length > 0) {
            if (args.length != 1) {
                System.out.println("Invalid arguments. Please only specify port for server to start.");
                System.exit(0);
            } else {
                port = args[0];
            }
        }

        // Start the server and wait for clients to connect
        try {
            InterFaceBoardMgr server = new BoardMessager();
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(port));
            registry.bind("Canvas", server);
            JOptionPane.showMessageDialog(null, "Start the Server");
            System.out.println("Server Running...");
        } catch (Exception e) {
            System.out.println("Error starting the server");
            System.exit(0);
        }
    }
}
