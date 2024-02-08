/**
 * Represents an HTTP response with a status code of 500 (Internal Server Error).
 * Some kind of error.
 */
public class InternalServerErrorResponse extends HttpResponse {
    private static final String HTML_500_PAGE =
            "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>500 - Internal Server Error</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            background-color: #f5f5f5;\n" +
                    "            text-align: center;\n" +
                    "            padding: 0;\n" +
                    "            margin: 0;\n" +
                    "        }\n" +
                    "\n" +
                    "        .container {\n" +
                    "            display: flex;\n" +
                    "            flex-direction: column;\n" +
                    "            align-items: center;\n" +
                    "            justify-content: center;\n" +
                    "            height: 100vh;\n" +
                    "        }\n" +
                    "\n" +
                    "        h1 {\n" +
                    "            font-size: 4em;\n" +
                    "            color: #333;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "\n" +
                    "        p {\n" +
                    "            font-size: 1.5em;\n" +
                    "            color: #666;\n" +
                    "        }\n" +
                    "\n" +
                    "        a {\n" +
                    "            color: #0077cc;\n" +
                    "            text-decoration: none;\n" +
                    "        }\n" +
                    "\n" +
                    "        a:hover {\n" +
                    "            text-decoration: underline;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <h1>500 - Internal Server Error</h1>\n" +
                    "        <p>Return to the <a href=\"/\">home page</a>.</p>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

    public InternalServerErrorResponse() {
        super(500, "Internal Server Error", HTML_500_PAGE.getBytes());
        this.contentType = "text/html";
    }
}
