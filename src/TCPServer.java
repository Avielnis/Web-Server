import Utils.ServerConfig;
import Utils.ServerLogger;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TCPServer {

    private final ServerConfig serverConfig = ServerConfig.getInstance();
    private final List<HTTPClient> clients = new ArrayList<>();
    private Semaphore semaphore;
    private final Object clientsAddLock = new Object();

    public TCPServer() throws IOException {
        if (! serverConfig.isLoadSuccess()) {
            ServerLogger.logger.severe("Failed loading config.ini");
            return;
        }

        semaphore = new Semaphore(serverConfig.getMaxThreads());
        try (ServerSocket serverSocket = new ServerSocket(serverConfig.getPort())) {
            ServerLogger.logger.info("Server starting on port: " + serverConfig.getPort());
            System.out.println("Server starting on port: " + serverConfig.getPort());
            System.out.println("Server url: http://127.0.0.1:"+serverConfig.getPort() + "\n");


            while (true) {
                try {
                    Socket newClientSocket = serverSocket.accept();
                    ServerLogger.logger.info("New client connected at port: " + newClientSocket.getPort());

                    // Limit number of concurrent threads
                    semaphore.acquire();
                    synchronized (clientsAddLock) {
                        clients.add(new HTTPClient(newClientSocket));
                    }
                    ServerLogger.logger.info("Number of active clients: " + clients.size());
                    new Thread(() -> {
                        try {
                            new HTTPClient(newClientSocket).run();
                        } finally {
                            semaphore.release();
                            removeClient(newClientSocket);
                        }
                    }).start();

                } catch (IOException e) {
                    ServerLogger.logger.info("Failed connecting new client");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    ServerLogger.logger.info("Server thread interrupted");
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
        ServerLogger.logger.info("Client disconnected: " + clientSocket.getPort());
        ServerLogger.logger.info("Number of active clients: " + clients.size());
        try {
            clientSocket.close();
        } catch (IOException e) {
            ServerLogger.logger.info("Failed closing client: " + clientSocket.getPort());
        }
    }

    public int getNumThreadsRunning() {
        synchronized (clientsAddLock) {
            return clients.size();
        }
    }
}
