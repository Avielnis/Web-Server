package http.response;

public abstract class HttpResponse {
    protected int statusCode;
    protected String statusMessage;
    protected String contentType;
    protected byte[] content;
    protected final String CRLF = "\r\n";
    protected boolean chunked;

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
        StringBuilder headerBuilder = new StringBuilder();

        // Start with the HTTP status line
        headerBuilder.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append(CRLF);

        // Add the Content-Type header
        headerBuilder.append("Content-Type: ").append(contentType).append(CRLF);

        // Conditionally add either Content-Length or Transfer-Encoding
        if (chunked) {
            // For chunked encoding, add the Transfer-Encoding header and do not add Content-Length
            headerBuilder.append("Transfer-Encoding: chunked").append(CRLF);
        } else {
            // For non-chunked responses, add the Content-Length header
            headerBuilder.append("Content-Length: ").append(content != null ? content.length : 0).append(CRLF);
        }

        // End with an additional CRLF to denote the end of the header section
        headerBuilder.append(CRLF);

        return headerBuilder.toString();
    }


    public byte[] getContent() {
        return content;
    }

    protected String getBody() {
        return content != null ? new String(content) : "";
    }

    public void setChunked() {
        this.chunked = true;
    }

    public void setContentTypeToImage() {
        this.contentType = "image/jpeg";
    }

    public void setContentTypeToIcon() {
        this.contentType = "image/x-icon";
    }

    @Override
    public String toString() {
        return getResponseHeader() + getBody();
    }
}
