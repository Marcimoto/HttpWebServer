package httpwebserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class HtmlFile {

    private InputStream in;
    private String htmlPage;

    public HtmlFile(int httpResponseStatusCode) {
        this.in = HtmlFile.class.getClassLoader().getResourceAsStream(httpResponseStatusCode + ".html");
        loadHtmlPage();
    }

    public String getHtmlPage() {
        return this.htmlPage;
    } 
    
    private void loadHtmlPage() {
        String content = "";
        try {
            content = readHtmlFile();
        } catch(IOException e) {
            e.printStackTrace();
        }
        this.htmlPage = content;
    }

    private String readHtmlFile() throws IOException {
        StringBuilder htmlPageContent = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        reader.lines().forEach(line -> htmlPageContent.append(line));
        return htmlPageContent.toString();
    }
}
