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
        this.contentType = "application/octet-stream";
    }

    public String getResponseHeader() {
        StringBuilder headerBuilder = new StringBuilder();

        headerBuilder.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append(CRLF);

        headerBuilder.append("Content-Type: ").append(contentType).append(CRLF);

        if (chunked) {
            headerBuilder.append("Transfer-Encoding: chunked").append(CRLF);
        } else {
            headerBuilder.append("Content-Length: ").append(content != null ? content.length : 0).append(CRLF);
        }

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

    public void setContentTypeToMessage() {
        this.contentType = "message/http";
    }

    public void setContentTypeToHtmlText() {
        this.contentType = "text/html";
    }

    @Override
    public String toString() {
        return getResponseHeader() + getBody();
    }
}
