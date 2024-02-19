# Multi-threaded TCP Web Server

## Description
This project is a Java-based multi-threaded TCP web server. It handles HTTP requests and responses over TCP connections, providing a lightweight framework for developing web applications or services.

## Features
- Multi-threaded processing for concurrent request handling.
- Basic HTTP request and response handling.
- Customizable server configurations.
- Simple logging mechanism for server activities.
- Support for static content serving.

## Requirements
- Java 11 or higher.

## Installation
1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Compile the Java files using your preferred Java compiler or build system.

## Usage
To start the server, run `compile.sh` and then `run.sh` to run without an IDE. 

## Configuration
Edit `config.ini` to change server parameters. 
You can set the server's port (`8080`), root directory (`www/lab/html`), default webpage (`index.html`), and maximum number of concurrent threads (`10`).

## Contributing
Contributions are welcome! Please fork the repository and open a pull request with your changes.
