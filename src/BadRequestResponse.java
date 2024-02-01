/**
 * Represents an HTTP response with a status code of 400 (Bad Request).
 * If the request’s format is invalid.
 */
public class BadRequestResponse extends HttpResponse {
    public BadRequestResponse() {
        super(400, "Bad Request", null);
        this.contentType = "text/html";
    }
}
