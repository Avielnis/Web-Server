package http.response;

public abstract class HttpResponse {
    protected int statusCode;
    protected String statusMessage;
    protected String contentType;
    protected byte[] content;
    protected final String CRLF = "\r\n";

    public HttpResponse(int statusCode, String statusMessage, byte[] content) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.content = content;
        this.setContentType(content);
    }

    private void setContentType(byte[] content) {
        // Set content type based on file extension or content (simplified example)
        // Default to application/octet-stream
        this.contentType = "application/octet-stream";
    }

    public String getResponseHeader() {
        return "HTTP/1.1 " + statusCode + " " + statusMessage + CRLF +
                "Content-Type: " + contentType + CRLF +
                "Content-Length: " + (content != null ? content.length : 0) + CRLF +
                CRLF;
    }

    public byte[] getContent() {
        return content;
    }

    protected String getBody() {
        return content != null ? new String(content) : "";
    }

    public void setContentTypeToImage(){
        this.contentType = "image/jpeg";
    }
    public void setContentTypeToIcon(){
        this.contentType = "image/x-icon";
    }
    @Override
    public String toString() {
        return getResponseHeader() + getBody();
    }
}
