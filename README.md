# Simple HTTP web server

Author: Marcel Unkauf

Date: 10.03.2021

### Implementation Approach

The server allows the user to explore the project through a browser. The client can either request a file or a directory. In case of a file, the requested file is simply returned in an HTTP response message. In case of a directory, the server computes an HTML Document, which lists the content of this directory and returns it in an HTTP response message to the client. Each file of the directory is wrapped in an `<a>`-tag, a click on one of these links will therefore lead to further GET-requests from the client to the server. This allows the user to browse the project from the root or a specified subfolder downwards. The server supports HTTP GET and HEAD requests.

The server checks for several errors, which are: 400, 404, 405 and 500. If one of theses cases occurs, the server response with an HTML error page, which expresses the error to the client. In some cases the server will response with a default message, for example in case of a GET-request with an unspecified resource; the default response message contains the content of the root.

I decided to run as well a server instance as each client request in separate threads. This way the execution of a server instance does not block the main thread of a program. Further, executing each request of a client in a thread allows the server to handle more requests.



### Technical Overview

In the root of the project is a *Dockerfile*, it can be used to create and run an Docker image which contains *Server.java*. *Server.java* can be found under `/src/main/java/httpwebserver/Server.java`.  When you run the *Dockerfile* you must connect to port 8080 of the Docker image.

I used Gradle as a build tool to add unit testing to the project. You can find a basic unit test under the following path: `/src/test/java/httpwebserver/ServerTest.java`.

To execute the unit test run `./gradlew test`

To execute *Server.java* run `./gradlew run`