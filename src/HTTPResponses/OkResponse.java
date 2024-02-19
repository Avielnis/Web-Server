package HTTPResponses;

/**
 * Represents an HTTP response with a status code of 200 (OK).
 * In case everything is okay.
 */
public class OkResponse extends HttpResponse {
    public OkResponse(byte[] content) {
        super(200, "OK", content);
    }

}
