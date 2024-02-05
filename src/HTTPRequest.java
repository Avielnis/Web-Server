import java.io.UnsupportedEncodingException;
import java.net.HttpRetryException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.net.URLDecoder;

public class HTTPRequest {
    private String type;
    private String requestedPage;
    private int contentLength;
    private String referer;
    private String userAgent;
    private HashMap<String, String> parameters;
    private String requestHeader;
    private String httpVersion;
    private boolean chunked;
    private String shortRequestHeader;


    public HTTPRequest(String requestHeader) throws HttpRetryException, UnsupportedEncodingException {
        this.requestHeader = requestHeader;
        parseRequestHeader();
    }

    private void parseRequestHeader() throws HttpRetryException, UnsupportedEncodingException {

        String[] lines = requestHeader.split("\r\n");
        shortRequestHeader = lines[0];
        String[] firstLineParts = lines[0].split(" ");
        httpVersion = firstLineParts[firstLineParts.length - 1];
        if (! httpVersion.equals("HTTP/1.0") && ! httpVersion.equals("HTTP/1.1")) {
            MyLogger.logger.severe("HTTP version is not 1.0 or 1.1 for request: " + requestHeader);
            throw new HttpRetryException("HTTP version is not 1.0 or 1.1", 500);
        }

        if (firstLineParts.length >= 2) {
            type = firstLineParts[0];
            String requestedPath = firstLineParts[1];
            int paramIndex = requestedPath.indexOf('?');
            if (paramIndex != - 1) {
                requestedPage = requestedPath.substring(0, paramIndex);
            } else {
                requestedPage = requestedPath;
            }
            requestedPage = requestedPage.substring(requestedPage.lastIndexOf('/'));
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
            } else if (line.startsWith("chunked: ")) {
                chunked = line.substring("chunked: ".length()).equals("yes");
            }
        }
        if (type.equals("GET")) {
            int paramIndex = lines[0].indexOf('?');
            if (paramIndex != - 1) {
                parseParams(lines[0].substring(paramIndex + 1));
            }
        }
        if (type.equals("POST")) {
            parseParams(lines[lines.length - 1]);
        }
    }

    public void parseParams(String paramString) throws UnsupportedEncodingException {
        parameters = new HashMap<>();
        int spaceIndex = paramString.indexOf(' ');
        if (spaceIndex != - 1) {
            paramString = paramString.substring(0, spaceIndex);
        }
        String[] paramPairs = paramString.split("&");
        for (String paramPair : paramPairs) {
            String[] keyValue = paramPair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = URLDecoder.decode(keyValue[1], "UTF-8");
                parameters.put(key, value);
            }
        }
    }

    public void parsePostParams_PostMan(String body, String boundary) {
        String[] parts = body.split(boundary);
        for (String part : parts) {
            String[] lines = part.split("\r\n");
            if (lines.length > 2) {
                String header = lines[1];
                String key = null;
                if (header.contains("name=\"")) {
                    key = header.substring(header.indexOf("name=\"") + 6, header.indexOf("\"", header.indexOf("name=\"") + 6));
                }
                StringBuilder value = new StringBuilder();
                for (int i = 3; i < lines.length - 1; i++) {

                    value.append(lines[i]);
                }
                if (key != null && ! key.isEmpty()) {
                    parameters.put(key, value.toString());
                }
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public String getRequestedPage() {
        return requestedPage;
    }

    public boolean isChunked() {
        return chunked;
    }

    public String getShortRequestHeader() {
        return shortRequestHeader;
    }

    public void setRequestedPage(String requestedPage) {
        this.requestedPage = requestedPage;
    }

    public boolean isPageExists() {
        if (requestedPage.equals("/")) {
            return true;
        }
        Path filePath = Paths.get(ServerConfig.getInstance().getRoot() + requestedPage.substring(requestedPage.lastIndexOf('/')));
        return Files.exists(filePath) && Files.isRegularFile(filePath);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public boolean isImage() {
        int dotIndex = requestedPage.lastIndexOf('.');
        if (dotIndex == - 1) {
            return false;
        }
        String extension = requestedPage.substring(dotIndex);
        if (extension.equals(".bmp") || extension.equals(".gif") || extension.equals(".png") || extension.equals(".jpg")) {
            return true;
        }
        return false;
    }

    public boolean isIcon() {
        int dotIndex = requestedPage.lastIndexOf('.');
        if (dotIndex == - 1) {
            return false;
        }
        String extension = requestedPage.substring(dotIndex);
        if (extension.equals(".ico")) {
            return true;
        }
        return false;
    }

    public boolean isHtmlText() {
        if (requestedPage.endsWith(".html") || requestedPage.equals("/")) {
            return true;
        }
        return false;
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

    @Override
    public String toString() {
        return requestHeader;
    }

}
