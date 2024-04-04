package ee.cc.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Requester {
    private static final String mainURL = "https://petstore.swagger.io/v2";

    public enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    public enum ContentType {
        FORM_DATA("application/x-www-form-urlencoded"),
        JSON("application/json");
        private final String contentType;

        ContentType(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public String toString() {
            return contentType;
        }
    }

    public HttpResponse<String> sendHttpRequest(String url, HttpMethod httpMethod) {
        return sendHttpRequest(url, httpMethod, null, null);
    }

    public HttpResponse<String> sendHttpRequest(String url, HttpMethod httpMethod, ContentType contentType, String content) {
        var client = HttpClient.newHttpClient();
        var httpRequestBuilder = HttpRequest.newBuilder().uri(URI.create(mainURL + url));
        HttpRequest httpRequest = null;
        switch (httpMethod) {
            case GET:
                httpRequest = httpRequestBuilder.GET().build();
                break;
            case POST:
                httpRequest = httpRequestBuilder
                        .POST(HttpRequest.BodyPublishers.ofString(content))
                        .header("Content-Type", contentType.toString())
                        .build();
                break;
            case DELETE:
                httpRequest = httpRequestBuilder
                        .DELETE()
                        .build();
                break;
            case PUT:
                httpRequest = httpRequestBuilder
                        .PUT(HttpRequest.BodyPublishers.ofString(content))
                        .header("Content-Type", contentType.toString())
                        .build();
        }

        try {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResponse<String> requestPet(String id) {
        return sendHttpRequest("/pet/" + id, HttpMethod.GET);
    }

    public HttpResponse<String> addPet(String content) {
        return sendHttpRequest("/pet", HttpMethod.POST, ContentType.JSON, content);
    }

    public HttpResponse<String> requestPetsByStatus(String petStatus) {
        return sendHttpRequest("/pet/findByStatus?status=" + petStatus, HttpMethod.GET);
    }

    public HttpResponse<String> deletePet(String id) {
        return sendHttpRequest("/pet/" + id, HttpMethod.DELETE);
    }

    public HttpResponse<String> updatePet(String content) {
        return sendHttpRequest("/pet", HttpMethod.PUT, ContentType.JSON, content);
    }

    //TODO: doesn't work yet (400: "org.jvnet.mimepull.MIMEParsingException: Missing start boundary"}
    public String uploadImage(String id, String metaData, String filePath) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(mainURL + "/pet/" + id + "/uploadImage");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("additionalMetadata", metaData, org.apache.http.entity.ContentType.TEXT_PLAIN);

        // This attaches the file to the POST:
        File f = new File(filePath);
        try {
            builder.addBinaryBody(
                    "file",
                    new FileInputStream(f),
                    org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM,
                    f.getName()
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        try {
            CloseableHttpResponse response = httpClient.execute(uploadFile);
            return response != null ? EntityUtils.toString(response.getEntity()) : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpResponse<String> updatePetNameStatus(String id, String content) {
        return sendHttpRequest("/pet/" + id, HttpMethod.POST, ContentType.FORM_DATA, content);
    }
}
