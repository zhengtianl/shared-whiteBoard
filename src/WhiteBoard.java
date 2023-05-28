import client.Client;
import client.InterFaceClient;
import server.BoardMessager;
import server.InterFaceBoardMgr;

import javax.swing.*;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main entry point to start the server and create or join a whiteboard.
 */
public class WhiteBoard {
    private static final String DEFAULT_SERVER_IP = "localhost";
    private static final String DEFAULT_SERVER_PORT = "3200";

    public static void main(String[] args) throws IOException {
        validateArgsLength(args);
        String role = args[0];

        switch (role) {
            case "StartServer":
                startServer(args);
                break;
            case "CreateWhiteBoard":
                createWhiteBoard(args);
                break;
            case "JoinWhiteBoard":
                joinWhiteBoard(args);
                break;
            default:
                System.out.println("Invalid mode");
                System.out.println("Running mode must be one of StartServer, CreateWhiteBoard and JoinWhiteBoard");
                System.exit(0);
        }
    }

    private static void validateArgsLength(String[] args) {
        if (args.length == 0) {
            System.out.println("Invalid arguments");
            System.exit(0);
        }
    }

    private static void startServer(String[] args) {
        String serverPort = DEFAULT_SERVER_PORT;
        if (args.length > 1) {
            if (args.length != 2) {
                System.out.println("Invalid arguments. Please specify the port for the server to start.");
                System.exit(0);
            } else {
            	serverPort = args[1];
            }
        }

        try {
            InterFaceBoardMgr server = new BoardMessager();
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(serverPort));
            registry.bind("Canvas", server);
            System.out.println("Server Running...");
        } catch (Exception e) {
            System.err.println("Error starting the server: " + e.getMessage());
            System.exit(0);
        }
    }

    private static InterFaceBoardMgr getServer(String serverIP, String serverPort) {
        try {
            String serverAddress = "//" + serverIP + ":"+ serverPort + "/Canvas";
            return (InterFaceBoardMgr) Naming.lookup(serverAddress);
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
            System.exit(0);
        }
        return null; // This is unreachable due to System.exit(0) above.
    }

    private static void clientLogin(InterFaceBoardMgr server, InterFaceClient client) {
        try {
            server.login(client);
        } catch(RemoteException e) {
            System.err.println("Login error: Unable to connect to server. " + e.getMessage());
            System.exit(0);
        }
    }

    private static void createWhiteBoard(String[] args) throws RemoteException {
        String serverIP = DEFAULT_SERVER_IP;
        String serverPort = DEFAULT_SERVER_PORT;
        String managerName = "manager";
        if (args.length > 1) {
            if (args.length != 4) {
                System.out.println("Invalid arguments. Please specify the server IP, port, and manager name.");
                System.exit(0);
            } else {
                serverIP = args[1];
                serverPort = args[2];
                managerName = args[3];
            }
        }

        InterFaceBoardMgr server = getServer(serverIP, serverPort);
        InterFaceClient client = new Client(server, managerName);
        clientLogin(server, client);
        client.renderUI();
    }

    private static void joinWhiteBoard(String[] args) throws IOException {
        String serverIP = DEFAULT_SERVER_IP;
        String serverPort = DEFAULT_SERVER_PORT;
        String username = "users";
        if (args.length > 1) {
            if (args.length != 4) {
                System.out.println("Invalid arguments. Please specify the server IP, port, and username.");
                System.exit(0);
            } else {
                serverIP = args[1];
                serverPort = args[2];
                username = args[3];
            }
        }

        InterFaceBoardMgr server = getServer(serverIP, serverPort);
        if (server.invalidUsername(username)) {
            System.out.println("The username has been taken! Please enter a new one.");
            System.exit(0);
        }
        InterFaceClient client = new Client(server, username);
        clientLogin(server, client);
        if (client.getAccess()) {
            client.renderUI();
        } else {
            server.quitClient(username);
            JOptionPane.showMessageDialog(null,
                    "Access denied! Please contact the manager to obtain access.",
                    "Warning", JOptionPane.INFORMATION_MESSAGE);
            client.forceQuit();
        }
    }
}
