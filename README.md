# Networks-Web-Server

Classes:

HTTPResponses.HttpResponse - father class for HTTPResponses,
this class construct the needed HTTP response

    HTTPResponses.BadRequestResponse;
    HTTPResponses.InternalServerErrorResponse;
    HTTPResponses.NotFoundResponse,
    HTTPResponses.NotImplementedResponse,
    HTTPResponses.OkResponse
All specific responses that exist.

HTTPClient - this is the class that each thread runs,
this is the main logic of each thread for each Socket.
decides what to do.

HTTPRequest - the class that parses a request.

Utils.ServerLogger - a class that logs running of the server
into a file called ServerLogs.log

Utils.ServerConfig - a singleton class that parses the config.ini file.

TCPServer - is the class of the main thread that 
accepts new socket connections, opens new threads for each connection
and closes the sockets on finish. 

Main - where the program begins and a TCPServer instance is created.

The design is the follows:
TCPServer loads the serverConfig and then listening
to new sockets.
flow:
    new socket connection arrives -> open new thread -> parse the request
    -> create response -> send response -> close socket

