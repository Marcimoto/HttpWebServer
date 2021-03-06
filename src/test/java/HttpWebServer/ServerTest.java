package HttpWebServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.file.Paths;

@TestInstance(Lifecycle.PER_CLASS)
public class ServerTest {

    Server server = null;
    String baseURL = null;

    @BeforeAll
    void setUp() {
        this.server = new Server();
        this.baseURL = String.format("http://localhost:%d/", server.getPort());
    }

    @Test
    void getRootTest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURL)).header("Content-Type", "txt/html")
                .version(Version.HTTP_1_1).GET().build();

        HttpResponse<Void> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(200, response.statusCode());
    }

    @Test
    void successfulHeadRequestTest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURL + "src/test/resources/existing.txt"))
                .header("Content-Type", "text/plain").version(Version.HTTP_1_1)
                .method("HEAD", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<Void> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(200, response.statusCode());
    }

    @Test
    void unsuccessfulHeadRequestTest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURL + "src/test/resources/notExisting.txt"))
                .header("Content-Type", "text/plain").version(Version.HTTP_1_1)
                .method("HEAD", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<Void> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    void successfulGetRequestFolderTest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURL + "src/test/resources/existing/"))
                .version(Version.HTTP_1_1).GET().build();

        HttpResponse<Void> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(200, response.statusCode());
    }

    @Test
    void successfulGetRequestFileTest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURL + "src/test/resources/existing.txt"))
                .header("Content-Type", "text/plain").version(Version.HTTP_1_1).GET().build();

        HttpResponse<Void> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(200, response.statusCode());
    }

    @Test
    void unsuccessfulGetRequestFolderTest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURL + "src/test/resources/notExisting/"))
                .version(Version.HTTP_1_1).GET().build();

        HttpResponse<Void> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    void unsuccessfulGetRequestFileTest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURL + "src/test/resources/notExisting.txt"))
                .header("Content-Type", "text/plain").version(Version.HTTP_1_1).GET().build();

        HttpResponse<Void> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(404, response.statusCode());
    }

    @Test
    void methodNotAllowedTest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<Void> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseURL)).header("Content-Type", "text/plain")
                    .version(Version.HTTP_1_1).PUT(BodyPublishers.ofFile(Paths.get("src/test/resources/existing.txt")))
                    .build();
            do {
                response = client.send(request, HttpResponse.BodyHandlers.discarding());
            } while (response == null);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(405, response.statusCode());
    }
}
