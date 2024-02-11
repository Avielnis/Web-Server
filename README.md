# Networks-Web-Server

Classes:

HttpResponse - father class for HTTPResponses,
this class construct the needed HTTP response

    BadRequestResponse;
    InternalServerErrorResponse;
    NotFoundResponse,
    NotImplementedResponse,
    OkResponse
All specific responses that exist.

HTTPClient - this is the class that each thread runs,
this is the main logic of each thread for each Socket.
decides what to do.

HTTPRequest - the class the parses a request.

MyLogger - a class that logs running of the server
into a file called ServerLogs.log

ServerConfig - a singleton class that parses the config.ini file.

TCPServer - is the class of the main thread that 
accepts new socket connections, opens new threads for each connection
and closes the sockets on finish. 


The design is the follows:
TCPServer loads the serverConfig and then listening
to new sockets.
flow:
    new socket connection arrives -> open new thread -> parse the request
    -> create response -> send response -> close socket

