package http.response;

/**
 * Represents an HTTP response with a status code of 200 (OK).
 * In case everything is okay.
 */
public class OkResponse extends HttpResponse {
    public OkResponse(String content) {
        super(200, "OK", content);
        this.contentType = "text/html";
    }

    public OkResponse(String content, boolean isImage, boolean isIcon) {
        super(200, "OK", content);
        if (isImage) {
            this.contentType = "image/jpeg";
        }
        if (isIcon) {
            this.contentType = "icon";
        }

    }
}
