package http.response;

public abstract class HttpResponse {
    protected int statusCode;
    protected String statusMessage;
    protected String contentType;
    protected String content;
    protected final String CRLF = "\r\n";

    public HttpResponse(int statusCode, String statusMessage, String content) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.content = content;
        this.setContentType(content);
    }

    private void setContentType(String content) {
        // Set content type based on file extension or content (simplified example)
        // Default to application/octet-stream
        this.contentType = "application/octet-stream";
    }

    public String getResponseHeader() {
        return "HTTP/1.1 " + statusCode + " " + statusMessage + CRLF +
                "Content-Type: " + contentType + CRLF +
                "Content-Length: " + (content != null ? content.length() : 0) + CRLF +
                CRLF;
    }

    public String getContent() {
        return content;
    }

    protected String getBody() {
        return content != null ? new String(content) : "";
    }

    @Override
    public String toString() {
        return getResponseHeader() + getBody();
    }
}
