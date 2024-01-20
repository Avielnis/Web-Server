import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {

    private final ServerConfig serverConfig = ServerConfig.getInstance();
    private final List<HTTPClient> clients = new ArrayList<>();
    private final Object clientsAddLock = new Object();

    public TCPServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(serverConfig.getPort())) {
            System.out.println("Server starting on port: " + serverConfig.getPort());

            while (true) {
                try {
                    Socket newClientSocket = serverSocket.accept();
                    System.out.println("New client connected at port: " + newClientSocket.getPort());

//                    synchronized (clientsAddLock) {
//                        while (clients.size() >= 10) {
//                            continue;
//                        }
//                        clients.add(newClientSocket);
//                    }

                    new Thread(new HTTPClient(this, newClientSocket)).start();

                } catch (IOException e) {
                    System.out.println("Failed connecting new client");
                }
            }

        }
    }

    public void registerClient(HTTPClient client) {
        clients.add(client);
    }

    public void removeClient(HTTPClient client) {
        System.out.println("client: " + client.getClientSocket().getPort() + " disconnected");
        try {
            client.getClientSocket().close();
        } catch (IOException e) {
            System.out.println("Failed closing client:" + client.getClientSocket().getPort());
        }
        clients.remove(client);
    }

    public int getNumThreadsRunning() {
        return clients.size();
    }

}
