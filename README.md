# Simple HTTP web server

Author: Marcel Unkauf
Date: 10.03.2021

### Implementation Approach

The client can either request a file or a directory. In case of a file the requested file is simply returned in a HTTP response message. In case of a directory the server computes a HTML Document from the content of this folder and return it also in an HTTP response message to the client. Each content item of the directory is wrapped in an `<a>`-tag, further clicks of one of these elements will therefore lead to consecutive GET-requests to the server. This allows the user to explore the project/server from its root downwards, ideally in a browser.

The server checks for several errors, which are: 400, 404, 405 and 500. If one of theses cases should occur during operation the server response with an HTML document which expresses the error to the client. In some cases the server will response with a default message, for example in case of a GET-request with an unspecified resource. The default response message serves the content of the root as a HTML document.

I decided to run as well a server instance as each client request in separate threads. This way the execution of a server instance does not block the main thread of the program. Further, executing each request of a client in a separate thread allows the server the handle more requests.

The main class *Server* uses two inner classes which a declared `private`, because there is no need to access the classes *BackgroundServer* or *ClientHandler* outside of *Server*.



### Technical Overview

The root of the project contains a *Dockerfile*, it can be used to create and run an image of *Server.java*, which can be found under `/src/main/java/httpwebserver.Server.java`.  When you run the *Dockerfile* you must connect to port 8080 of the Docker image.

I used Gradle as a build tool to add unit testing to the project. You can find a basic unit test class under the following path: `/src/test/java/httpwebserver/ServerTest.java`.