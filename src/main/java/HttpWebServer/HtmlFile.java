package httpwebserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class HtmlFile {

    private InputStream in;

    public HtmlFile(int httpResponseStatusCode) {
        System.out.println(httpResponseStatusCode + ".html");
        this.in = HtmlFile.class.getClassLoader().getResourceAsStream(httpResponseStatusCode + ".html");
    }
    
    public String getHtmlPageContent() {
        String content = "";
        try {
            content = readHtmlFile();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private String readHtmlFile() throws IOException {
        StringBuilder htmlPageContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        reader.lines().forEach(line -> htmlPageContent.append(line));
        return htmlPageContent.toString();
    }
}
