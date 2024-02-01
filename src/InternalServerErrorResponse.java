/**
 * Represents an HTTP response with a status code of 500 (Internal Server Error).
 * Some kind of error.
 */
public class InternalServerErrorResponse extends HttpResponse {
    public InternalServerErrorResponse() {
        super(500, "Internal Server Error", null);
        this.contentType = "text/html";
    }
}
