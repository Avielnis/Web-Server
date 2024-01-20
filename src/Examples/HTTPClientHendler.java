package Examples;

import java.io.*;
import java.net.Socket;

public class HTTPClientHendler implements Runnable {
    private Socket clientSocket;
    private final String htmlFilePath = "index.html"; // Path to your HTML file

    public HTTPClientHendler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            // Determine the request type
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String requestLine = in.readLine(); // Read the request line
            String[] requestParts = requestLine.split(" ");
            String requestedResource = requestParts.length > 1 ? requestParts[1] : "";

            if (requestedResource.endsWith(".jpg")) {
                sendImage(requestedResource); // Serve image file
            } else {
                sendHTML(); // Serve HTML file
            }

            clientSocket.close();


        } catch (IOException e) {
            System.out.println("client: " + clientSocket.getPort() + " disconnected");
            e.printStackTrace();
        }
    }

    private void sendImage(String imagePath) throws IOException {
        imagePath = imagePath.substring(1);
        File file = new File(imagePath);
        if (! file.exists()) {
            send404();
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // HTTP Headers
            String headers = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: image/jpeg\r\n" + // Assuming JPEG image
                    "Content-Length: " + file.length() + "\r\n" +
                    "\r\n";
            outputStream.write(headers.getBytes());

            // Send the image content
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != - 1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private void sendHTML() throws IOException {
        try (FileInputStream fileIn = new FileInputStream(htmlFilePath);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {

            // Read HTML file content
            byte[] fileContent = new byte[fileIn.available()];
            fileIn.read(fileContent);

            // Send HTTP response headers
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Content-Length: " + fileContent.length);
            out.println(); // Blank line between headers and content

            // Send HTML file content
            out.write(new String(fileContent, "UTF-8"));
            out.flush();
        }
    }

    private void send404() throws IOException {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/html");
            out.println();
            out.println("<html><body><h1>404 Not Found</h1></body></html>");
            out.flush();
        }
    }
}
