import java.io.*;
import java.net.HttpRetryException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HTTPClient implements Runnable {
    private Socket clientSocket;
    private HTTPRequest request;
    private int port;
    private final ServerConfig serverConfig = ServerConfig.getInstance();

    public HTTPClient(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.port = clientSocket.getPort();

    }


    @Override
    public void run() {
        try {
            request = getClientRequest();

            System.out.println(request.getShortRequestHeader() + '\n');
            MyLogger.logger.info("Requested: " + request.getRequestHeader());

            String requestType = request.getType();
            if (requestType.equals("GET")) {
                sendBackGET();
            } else if (requestType.equals("POST")) {
                sendBackPOST();
            } else if (requestType.equals("HEAD")) {
                sendBackHEAD();

            } else if (requestType.equals("TRACE")) {
                sendBackTrace();
            } else {
                sendBack501();
            }


        } catch (NullPointerException e) {
            MyLogger.logger.severe("Input request is null at port: " + port);
            try {
                request = new HTTPRequest(null);
            } catch (Exception e2) {
                MyLogger.logger.severe("Failed creating a null request");
                return;
            }
            sendErrorResponse(new InternalServerErrorResponse());

        } catch (HttpRetryException | UnsupportedEncodingException e) {
            MyLogger.logger.severe("failed parsing request");
            sendErrorResponse(new BadRequestResponse());

        } catch (IOException e) {
            MyLogger.logger.severe("failed parsing request");
            sendErrorResponse(new InternalServerErrorResponse());
        }
    }

    private void sendBackGET() throws IOException {
        byte[] fileContent = loadFileContent();
        if (fileContent == null) {
            sendResponse(new InternalServerErrorResponse());
        }
        HttpResponse response = new OkResponse(fileContent);
        factoryRequestType(response);
        sendResponse(response);
    }

    private void sendBackPOST() throws IOException {
        byte[] fileContent = loadFileContent();
        HttpResponse response = null;
        if (request.getParameters().size() != 0) {
            String htmlParamsPage = injectHTMLParams(fileContent);
            response = new OkResponse(htmlParamsPage.getBytes());
        } else {
            response = new OkResponse(fileContent);
        }
        factoryRequestType(response);
        sendResponse(response);

    }

    private String injectHTMLParams(byte[] fileContent) {
        String htmlTemplate = new String(fileContent);
        HashMap<String, String> params = request.getParameters();
        String result = params.entrySet().stream()
                .map(entry -> "<p>" + entry.getKey() + ": " + entry.getValue() + "</p><br/>")
                .collect(Collectors.joining("\n"));
        result = "<div>" + result + "</div></body>";
        htmlTemplate = htmlTemplate.replace("</body>", result);
        return htmlTemplate;
    }

    private void sendBackHEAD() throws IOException {
        byte[] fileContent = loadFileContent();
        HttpResponse response = new OkResponse(fileContent);
        PrintWriter textStream = new PrintWriter(clientSocket.getOutputStream(), true);
        textStream.println(response.getResponseHeader());
        System.out.println(response.getResponseHeader());
    }

    private void sendBackTrace() throws IOException {
        HttpResponse response = new OkResponse(request.getRequestHeader().getBytes());
        response.setContentTypeToMessage();
        sendResponse(response);
    }

    private void sendBack501() throws IOException {
        sendResponse(new NotImplementedResponse());
    }

    private void sendResponse(HttpResponse response) throws IOException {
        OutputStream byteStream = clientSocket.getOutputStream();
        PrintWriter textStream = new PrintWriter(clientSocket.getOutputStream(), true);
        if (! request.isChunked()) {
            if (request.isImage() || request.isIcon()) {
                byteStream.write(response.getResponseHeader().getBytes());
                byteStream.write(response.getContent());
            } else {
                textStream.println(response);
            }
            System.out.println(response.getResponseHeader());
            return;
        }
        response.setChunked();
        String responseHeader = response.getResponseHeader();
        byteStream.write(responseHeader.getBytes());

        byte[] responseBytes = response.getContent();
        int offset = 0;
        int bufferSize = 1024; // Size of each chunk

        // Send chunks while there's data to send
        while (offset < responseBytes.length) {
            int chunkSize = Math.min(bufferSize, responseBytes.length - offset);
            byte[] chunk = Arrays.copyOfRange(responseBytes, offset, offset + chunkSize);

            textStream.println(Integer.toHexString(chunk.length));
            byteStream.write(chunk);
            byteStream.flush();
            textStream.println(); // End of chunk

            offset += chunkSize;
        }
        // Send a zero-length chunk to indicate the end of the response
        textStream.println("0");
        textStream.println();
        System.out.println(response.getResponseHeader());
    }

    public void sendErrorResponse(HttpResponse response) {
        try {
            PrintWriter textStream = new PrintWriter(clientSocket.getOutputStream(), true);
            textStream.println(response);
            System.out.println(response.getResponseHeader());
        } catch (IOException e) {
            MyLogger.logger.severe("Filed sending Error response");
        }
    }

    private HTTPRequest getClientRequest() throws IOException {
        String fullRequest = readFullRequest();
        return new HTTPRequest(fullRequest);
    }

    public void factoryRequestType(HttpResponse response) {
        if (request.isHtmlText()) {
            response.setContentTypeToHtmlText();
        } else if (request.isImage()) {
            response.setContentTypeToImage();
        } else if (request.isIcon()) {
            response.setContentTypeToIcon();
        }
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

    private byte[] loadFileContent() throws IOException {
        String pageFilePath = serverConfig.getRoot() + "/" + serverConfig.getDefaultPage();
        if (! request.getRequestedPage().equals("/")) {
            pageFilePath = serverConfig.getRoot() + request.getRequestedPage();
        }

        if (! request.isPageExists()) {
            sendResponse(new NotFoundResponse());
            MyLogger.logger.info("Didnt find: " + request.getRequestedPage());
            return null;
        }

        File htmlFile = new File(pageFilePath);
        FileInputStream fileInputStream = new FileInputStream(htmlFile);
        byte[] fileContent = new byte[(int) htmlFile.length()];
        fileInputStream.read(fileContent);
        return fileContent;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
