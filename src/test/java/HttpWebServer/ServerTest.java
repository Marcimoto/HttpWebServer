package HttpWebServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

@TestInstance(Lifecycle.PER_CLASS)
public class ServerTest {

    Server server = null;
    String url = null;
    HttpClient client;

    @BeforeAll
    void setUp() {
        this.server = new Server();
        url = String.format("http://localhost:%d/", server.getPort());
        client = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();
    }

    @Test
    void getRootTest() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "txt/html")
                .version(Version.HTTP_1_1).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    // @Test
    // void successfulGetDirectoryTest() {
    // String baseURL = String.format("http://localhost:%d/");

    // }

    // /**
    // * A helper method to simulate HTTP GET requests to urls.
    // *
    // * @param url
    // * @return String object containing the body of the HTTP response.
    // */
    // private String httpGet(String url) {
    // var uri = URI.create(url);
    // var client = HttpClient.newHttpClient();
    // var request = HttpRequest.newBuilder().uri(uri).GET().build();
    // try {
    // return client.send(request, BodyHandlers.ofString()).body();
    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // return null;
    // }
    // }

}
