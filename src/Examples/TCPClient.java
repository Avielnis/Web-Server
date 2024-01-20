package Examples;

import java.io.*;
import java.net.*;

public class TCPClient {

    private final String serverAddress = "127.0.0.1";
    private final int port = 9922;
    private Socket serverSocket;

    public TCPClient() throws IOException {

        try {
            serverSocket = new Socket(serverAddress, port);
            PrintWriter serverInput = new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader serverOutput = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try {
                            System.out.println(serverOutput.readLine());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }).start();
            while (true) {
                String userInput = stdIn.readLine();
                serverInput.println(userInput);
            }


        } catch (IOException e) {
            System.out.println("failed connecting to server");
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

}


