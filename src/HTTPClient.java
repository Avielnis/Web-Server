import http.response.*;

import java.io.*;
import java.net.Socket;

public class HTTPClient implements Runnable {
    private Socket clientSocket;
    private HTTPRequest request;
    private int port;
    private PrintWriter textStream;
    private OutputStream mediaStream;
    private final ServerConfig serverConfig = ServerConfig.getInstance();

    public HTTPClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.port = clientSocket.getPort();

    }


    @Override
    public void run() {
        try {

            mediaStream = clientSocket.getOutputStream();
            textStream = new PrintWriter(clientSocket.getOutputStream(), true);

            request = getClientRequest();

            System.out.println(request.getRequestHeader());
            MyLogger.logger.info("Requested: " + request.getRequestHeader());


            if (request.getType().equals("GET")) {
                sendBackGET();
            } else if (request.getType().equals("POST")) {
                sendBackPOST(request);
            } else {
                sendBack501();
            }


        } catch (IOException e) {
            MyLogger.logger.severe("Failed handling request at port: " + port);
        } catch (NullPointerException e) {
            MyLogger.logger.severe("Input request is null at port: " + port);
        }
    }

    private void sendBackGET() throws IOException {
        String pageFilePath = serverConfig.getRoot() + "/" + serverConfig.getDefaultPage();
        if (! request.getRequestedPage().equals("/")) {
            pageFilePath = serverConfig.getRoot() + request.getRequestedPage();
        }
        File htmlFile = new File(pageFilePath);
        if (! htmlFile.exists()) {
            textStream.println(new NotFoundResponse());
        }
        FileInputStream fileInputStream = new FileInputStream(htmlFile);
        byte[] fileContent = new byte[(int) htmlFile.length()];
        fileInputStream.read(fileContent);
        HttpResponse response = new OkResponse(fileContent);

        if (request.isPageImage()) {
            sendImage(response);
        } else {
            // send .html
            textStream.println(response);
        }
        System.out.println(response.getResponseHeader());
    }

    private void sendBackPOST(HTTPRequest request) throws IOException {

    }

    private void sendBack501() throws IOException {
        textStream.println(new NotImplementedResponse());
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
