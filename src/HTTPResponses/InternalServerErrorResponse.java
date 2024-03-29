package HTTPResponses;

/**
 * Represents an HTTP response with a status code of 500 (Internal Server Error).
 * Some kind of error.
 */
public class InternalServerErrorResponse extends HttpResponse {
    private static final String HTML_500_PAGE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>500 - Internal Server Error</title>
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
                    <h1>500 - Internal Server Error</h1>
                    <p>Return to the <a href="/">home page</a>.</p>
                </div>
            </body>
            </html>
            """;


    public InternalServerErrorResponse() {
        super(500, "Internal Server Error", HTML_500_PAGE.getBytes());
        this.contentType = "text/html";
    }
}
