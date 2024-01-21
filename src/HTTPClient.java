import http.response.*;

import java.io.*;
import java.net.Socket;

public class HTTPClient implements Runnable {
    private TCPServer tcpServer;
    private Socket clientSocket;
    private HTTPRequest request;
    private int port;
    private PrintWriter textStream;
    private OutputStream mediaStream;
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

            mediaStream = clientSocket.getOutputStream();
            textStream = new PrintWriter(clientSocket.getOutputStream(), true);

            request = getClientRequest();

            System.out.println(request.getRequestHeader());
            MyLogger.logger.info("Requested: " + request.getRequestHeader());

            if (! request.getHttpVersion().equals("HTTP/1.0") &&
                    ! request.getHttpVersion().equals("HTTP/1.1")) {
                throw new IOException();
            }

            if (request.getType().equals("GET")) {
                sendBackGET(request.getRequestedPage());
            } else if (request.getType().equals("POST")) {
                sendBackPOST(request);
            }
            else {
                sendBack501();
            }


        } catch (IOException e) {
            MyLogger.logger.severe("Failed handling request at port: " + port);
        } catch (NullPointerException e) {
            MyLogger.logger.severe("Input request is null at port: " + port);
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
            textStream.println(new NotFoundResponse());
        }
        FileInputStream fileInputStream = new FileInputStream(htmlFile);
        byte[] fileContent = new byte[(int) htmlFile.length()];
        fileInputStream.read(fileContent);
        HttpResponse response = new OkResponse(fileContent);

        if (pagePath.contains("image")) {
            sendImage(response);
        } else {
            textStream.println(response);
        }
        System.out.println(response.getResponseHeader());
    }

    private void sendBackPOST(HTTPRequest request) throws IOException {

    }

    private void sendBack501() throws IOException {
        textStream.println(new NotImplementedResponse());
    }

    private void closeClient() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            MyLogger.logger.severe("Field closing client at port: " + port);
//            System.out.println("Field closing client at port: " + port);
        }
        tcpServer.removeClient(this);

    }

    private void sendImage(HttpResponse response) throws IOException {
        response.setContentTypeToImage();
        mediaStream.write(response.getResponseHeader().getBytes());
        mediaStream.write(response.getContent());
    }

    private HTTPRequest getClientRequest() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String line = reader.readLine();
        return new HTTPRequest(line);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
