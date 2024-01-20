package http.response;

/**
 * Represents an HTTP response with a status code of 404 (Not Found).
 * If the file was not found.
 */
public class NotFoundResponse extends HttpResponse {
    public NotFoundResponse() {
        super(404, "Not Found", "<html><body><h1>404 Not Found</h1></body></html>".getBytes());
        this.contentType = "text/html";
    }
}