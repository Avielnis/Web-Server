import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ServerConfig {
    private static ServerConfig instance;
    private int port;
    private String root;
    private String defaultPage;
    private int maxThreads;
    private final String CONFIG_FILE_PATH = "config.ini";

    private ServerConfig() {
        loadConfig();
    }

    public static ServerConfig getInstance() {
        if (instance == null) {
            instance = new ServerConfig();
        }
        return instance;
    }

    private void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("port")) {
                    port = Integer.parseInt(line.split("=")[1].trim());
                } else if (line.startsWith("root")) {
                    root = System.getProperty("user.home") + line.split("=")[1].trim().substring(1);
                } else if (line.startsWith("defaultPage")) {
                    defaultPage = line.split("=")[1].trim();
                } else if (line.startsWith("maxThreads")) {
                    maxThreads = Integer.parseInt(line.split("=")[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public String getRoot() {
        return root;
    }

    public String getDefaultPage() {
        return defaultPage;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public static void main(String[] args) {
        ServerConfig serverConfig = ServerConfig.getInstance();

        int port = serverConfig.getPort();
        String root = serverConfig.getRoot();
        String defaultPage = serverConfig.getDefaultPage();
        int maxThreads = serverConfig.getMaxThreads();

        System.out.println("Port: " + port);
        System.out.println("Root: " + root);
        System.out.println("Default Page: " + defaultPage);
        System.out.println("Max Threads: " + maxThreads);
    }
}
