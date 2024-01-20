import http.response.*;

import java.io.*;
import java.net.Socket;

public class HTTPClient implements Runnable {
    private TCPServer tcpServer;
    private Socket clientSocket;
    private int port;
    private PrintWriter out;
    private final ServerConfig serverConfig = ServerConfig.getInstance();

    public HTTPClient(TCPServer tcpServer, Socket clientSocket) {
        this.tcpServer = tcpServer;
        this.clientSocket = clientSocket;
        this.port = clientSocket.getPort();
        tcpServer.registerClient(this);
    }


    @Override
    public void run() {
        try {
            // Determine the request type
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            String requestLine = in.readLine(); // Read the request line GET or POST
            String[] requestParts = requestLine.split(" ");
            System.out.println(requestLine);

            if (! requestParts[requestParts.length - 1].equals("HTTP/1.0") &&
                    ! requestParts[requestParts.length - 1].equals("HTTP/1.1")) {
                throw new IOException();
            }

            if (requestParts[0].equals("GET")) {
                sendBackGET(requestParts[1]);
            } else if (requestParts[0].equals("POST")) {
                sendBackPOST(requestParts);
            }
            // Unknown method
            else {
                sendBack501();
            }


        } catch (IOException e) {
            System.out.println("Failed handling request at port: " + port);
        } catch (NullPointerException e) {
            System.out.println("Input request is null at port: " + port);
        }
        closeClient();
    }

    private void sendBackGET(String pagePath) throws IOException {
        String pageFilePath = serverConfig.getRoot() + "/" + serverConfig.getDefaultPage();
        if (! pagePath.equals("/")) {
            pageFilePath = serverConfig.getRoot() + pagePath;
        }
        File htmlFile = new File(pageFilePath);
        if (! htmlFile.exists()) {
            out.println(new NotFoundResponse());
        }
        FileInputStream fileInputStream = new FileInputStream(htmlFile);
        byte[] fileContent = new byte[(int) htmlFile.length()];
        fileInputStream.read(fileContent);
        HttpResponse response = new OkResponse(fileContent);
        System.out.println(response.getResponseHeader());
        out.println(response);
    }

    private void sendBackPOST(String[] requestParts) throws IOException {

    }

    private void sendBack501() throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(new NotImplementedResponse());
    }

    private void closeClient() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Field closing client at port: " + port);
        }
        tcpServer.removeClient(this);

    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
