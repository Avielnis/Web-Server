import http.response.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

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

            System.out.println(request.getShortRequestHeader());
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

        if (! request.isPageExists()) {
            sendResponse(new NotFoundResponse());
            MyLogger.logger.info("Didnt find: " + request.getRequestedPage());
            return;
        }

        File htmlFile = new File(pageFilePath);
        FileInputStream fileInputStream = new FileInputStream(htmlFile);
        byte[] fileContent = new byte[(int) htmlFile.length()];
        fileInputStream.read(fileContent);
        HttpResponse response = new OkResponse(fileContent);

        if (request.isImage()) {
            response.setContentTypeToImage();
        } else if (request.isIcon()) {
            response.setContentTypeToIcon();
        }
        sendResponse(response);
    }


    private void sendBackPOST(HTTPRequest request) throws IOException {
        if (! request.isPageExists()) {
            sendResponse(new NotFoundResponse());
            MyLogger.logger.info("Didnt find: " + request.getRequestedPage());
            return;
        }
        request.setRequestedPage("params_info.html");
        String pageFilePath = serverConfig.getRoot() + "/" + request.getRequestedPage();
        File paramsHtmlFile = new File(pageFilePath);
        FileInputStream fileInputStream = new FileInputStream(paramsHtmlFile);
        byte[] fileContent = new byte[(int) paramsHtmlFile.length()];
        fileInputStream.read(fileContent);
        String htmlParamsPage = injectHTMLParams(request, fileContent);
        HttpResponse response = new OkResponse(htmlParamsPage.getBytes());
        sendResponse(response);

    }

    private String injectHTMLParams(HTTPRequest request, byte[] fileContent) {
        String htmlTemplate = new String(fileContent);
        HashMap<String, String> params = request.getParameters();
        String result = params.entrySet().stream()
                .map(entry -> "<p>" + entry.getKey() + ": " + entry.getValue() + "</p>")
                .collect(Collectors.joining("\n"));
        htmlTemplate = htmlTemplate.replace("{content}", result);
        return htmlTemplate;
    }

    private void sendBack501() throws IOException {
        sendResponse(new NotImplementedResponse());
    }

    private void sendResponse(HttpResponse response) throws IOException {
        if (! request.isChunked()) {
            if (request.isImage() || request.isIcon()) {
                mediaStream.write(response.getResponseHeader().getBytes());
                mediaStream.write(response.getContent());
            } else {
                textStream.println(response);
            }
            System.out.println(response.getResponseHeader());
            return;
        }
        response.setChunked();
        // Convert response to a string representation
        String responseHeader = response.getResponseHeader();
        mediaStream.write(responseHeader.getBytes());

        byte[] responseBytes = response.getContent();
        int offset = 0;
        int bufferSize = 1024; // Size of each chunk

        // Send chunks while there's data to send
        while (offset < responseBytes.length) {
            int chunkSize = Math.min(bufferSize, responseBytes.length - offset);
            byte[] chunk = Arrays.copyOfRange(responseBytes, offset, offset + chunkSize);

            // Send the size of the chunk in hexadecimal
            textStream.println(Integer.toHexString(chunk.length));
            // Send the chunk itself
            mediaStream.write(chunk);
            mediaStream.flush();
            textStream.println(); // End of chunk

            offset += chunkSize;
        }

        // Send a zero-length chunk to indicate the end of the response
        textStream.println("0");
        textStream.println(); // End of the chunked response
        System.out.println(response.getResponseHeader());
    }


    private HTTPRequest getClientRequest() throws IOException {
        String fullRequest = readFullRequest();
        return new HTTPRequest(fullRequest);
    }

    private String readFullRequest() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        StringBuilder request = new StringBuilder();
        String line;
        int contentLength = 0;
        while (! (line = reader.readLine()).isBlank()) {
            request.append(line).append("\r\n");
            if (line.contains("Content-Length: ")) {
                contentLength = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
            }
        }
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String postBody = new String(buffer);
        request.append(postBody + "\r\n");
        return request.toString();
    }


    public Socket getClientSocket() {
        return clientSocket;
    }
}
