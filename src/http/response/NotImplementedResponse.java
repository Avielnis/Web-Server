package http.response;

/**
 * Represents an HTTP response with a status code of 501 (Not Implemented).
 * If the method used is unknown (a Method is like “GET”).
 */
public class NotImplementedResponse extends HttpResponse {
    public NotImplementedResponse() {
        super(501, "Not Implemented", "<html><body><h1>501 Not Implemented</h1></body></html>".getBytes());
        this.contentType = "text/html";
    }
}