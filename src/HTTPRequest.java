import java.util.HashMap;

public class HTTPRequest {
    private String type;
    private String requestedPage;
    private boolean isImage;
    private int contentLength;
    private String referer;
    private String userAgent;
    private HashMap<String, String> parameters;
    private String requestHeader;
    private String httpVersion;


    public HTTPRequest(String requestHeader) {
        this.requestHeader = requestHeader;
        parseRequestHeader();
    }

    private void parseRequestHeader() {

        String[] lines = requestHeader.split("\r\n");

        // First line contains the request type and requested page
        String[] firstLineParts = lines[0].split(" ");
        httpVersion = firstLineParts[firstLineParts.length-1];
        if (firstLineParts.length >= 2) {
            type = firstLineParts[0];
            // Extract the requested page portion without parameters
            String requestedPath = firstLineParts[1];
            int paramIndex = requestedPath.indexOf('?');
            if (paramIndex != - 1) {
                requestedPage = requestedPath.substring(0, paramIndex);
            } else {
                requestedPage = requestedPath;
            }
        }

        // Parse other headers
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(line.substring("Content-Length: ".length()));
            } else if (line.startsWith("Referer: ")) {
                referer = line.substring("Referer: ".length());
            } else if (line.startsWith("User-Agent: ")) {
                userAgent = line.substring("User-Agent: ".length());
            }
        }

        // Parse parameters from the requested page (if any)
        parameters = new HashMap<>();
        String requestLine = lines[0];
        int paramIndex = requestLine.indexOf('?');
        if (paramIndex != -1) {
            String paramString = requestLine.substring(paramIndex + 1);
            String[] paramPairs = paramString.split("&");
            for (String paramPair : paramPairs) {
                String[] keyValue = paramPair.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    // Getter methods for accessing parsed data
    public String getType() {
        return type;
    }

    public String getRequestHeader() {
        return requestHeader;
    }
    public String getRequestedPage() {
        return requestedPage;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public boolean isImage() {
        return isImage;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getReferer() {
        return referer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public static void main(String[] args) {
        String requestHeader = "GET /index.html?Name=Aviel&Last=Nisanov HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Referer: http://referer.com\r\n" +
                "User-Agent: Mozilla/5.0\r\n" +
                "Content-Length: 0\r\n\r\n";

        HTTPRequest httpRequest = new HTTPRequest(requestHeader);

        System.out.println("Type: " + httpRequest.getType());
        System.out.println("Requested Page: " + httpRequest.getRequestedPage());
        System.out.println("Is Image: " + httpRequest.isImage());
        System.out.println("Content Length: " + httpRequest.getContentLength());
        System.out.println("Referer: " + httpRequest.getReferer());
        System.out.println("User Agent: " + httpRequest.getUserAgent());
        System.out.println("Parameters: " + httpRequest.getParameters());
    }
}
