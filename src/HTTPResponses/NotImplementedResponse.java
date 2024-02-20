package HTTPResponses;

/**
 * Represents an HTTP response with a status code of 501 (Not Implemented).
 * If the method used is unknown
 */
public class NotImplementedResponse extends HttpResponse {
    private static final String HTML_501_PAGE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>501 - Not Implemented</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f5f5f5;
                        text-align: center;
                        padding: 0;
                        margin: 0;
                    }

                    .container {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        height: 100vh;
                    }

                    h1 {
                        font-size: 4em;
                        color: #333;
                        margin-bottom: 20px;
                    }

                    p {
                        font-size: 1.5em;
                        color: #666;
                    }

                    a {
                        color: #0077cc;
                        text-decoration: none;
                    }

                    a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>501 - Not Implemented</h1>
                    <p>The requested functionality is not implemented on this server.</p>
                    <p>Return to the <a href="/">home page</a>.</p>
                </div>
            </body>
            </html>
            """;


    public NotImplementedResponse() {
        super(501, "Not Implemented", HTML_501_PAGE.getBytes());
        this.contentType = "text/html";
    }
}