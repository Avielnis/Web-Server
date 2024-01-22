import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TCPServer {

    private final ServerConfig serverConfig = ServerConfig.getInstance();
    private final List<HTTPClient> clients = new ArrayList<>();
    private final Semaphore semaphore;
    private final Object clientsAddLock = new Object();

    public TCPServer() throws IOException {
        MyLogger.removeExsitingHandlaers();
        semaphore = new Semaphore(serverConfig.getMaxThreads());

        try (ServerSocket serverSocket = new ServerSocket(serverConfig.getPort())) {
            MyLogger.logger.info("Server starting on port: " + serverConfig.getPort());
            System.out.println("Server starting on port: " + serverConfig.getPort());

            while (true) {
                try {
                    Socket newClientSocket = serverSocket.accept();
                    MyLogger.logger.info("New client connected at port: " + newClientSocket.getPort());

                    // Limit number of concurrent threads
                    semaphore.acquire();
                    synchronized (clientsAddLock) {
                        clients.add(new HTTPClient(newClientSocket));
                    }
                    MyLogger.logger.info("Number of active clients: " + clients.size());
                    new Thread(() -> {
                        try {
                            new HTTPClient(newClientSocket).run();
                        } finally {
                            semaphore.release();
                            removeClient(newClientSocket);
                        }
                    }).start();

                } catch (IOException e) {
                    MyLogger.logger.info("Failed connecting new client");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    MyLogger.logger.info("Server thread interrupted");
                }
            }

        }
    }

    public void registerClient(HTTPClient client) {
        synchronized (clientsAddLock) {
            clients.add(client);
        }
    }

    public void removeClient(Socket clientSocket) {
        synchronized (clientsAddLock) {
            clients.removeIf(client -> client.getClientSocket().equals(clientSocket));
        }
        MyLogger.logger.info("Client disconnected: " + clientSocket.getPort());
        MyLogger.logger.info("Number of active clients: " + clients.size());
        try {
            clientSocket.close();
        } catch (IOException e) {
            MyLogger.logger.info("Failed closing client: " + clientSocket.getPort());
        }
    }

    public int getNumThreadsRunning() {
        synchronized (clientsAddLock) {
            return clients.size();
        }
    }
}
