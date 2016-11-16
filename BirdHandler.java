import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.BasicBSONList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BirdHandler implements HttpHandler {

    private MongoCollection<Document> mongoCollection = new MongoClient("localhost", 27017)
            .getDatabase("saltside").getCollection("birds");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
    private Document birdDocument = new Document("name", "");
    private Document visibleDocument = new Document("visible", true);
    private Bson idProjection = Projections.include("name");

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        switch (httpExchange.getRequestMethod()) {

            case "POST":
                httpPost(httpExchange);
                break;
            case "DELETE":
                httpDelete(httpExchange);
                break;
            case "GET":
                httpGet(httpExchange);
                break;
        }
    }

    private void httpGet(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();
        String birdId = path.substring(6);
        if (birdId.length() == 0 || "/".equals(birdId)) {
            httpGetAll(httpExchange);
        } else {
            birdId = birdId.replaceFirst("/", "");
            httpGetOne(httpExchange, birdId);
        }
    }

    //GET /birds
    private void httpGetAll(HttpExchange httpExchange) throws IOException {

        FindIterable<Document> documents = mongoCollection.find(visibleDocument).projection(idProjection);
        BasicBSONList bsonList = new BasicBSONList();
        int keyIndex = 0;
        for (Document document : documents) {
            bsonList.put(keyIndex++, document.get("name").toString());
        }

        String response = JSON.serialize(bsonList);
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.getResponseBody().write(response.getBytes());
        httpExchange.getResponseBody().close();
    }

    //GET /birds/{id}
    private void httpGetOne(HttpExchange httpExchange, String birdId) throws IOException {

        birdDocument.put("name", birdId);
        Document document = mongoCollection.find(birdDocument).first();
        if (document == null) {
            httpExchange.sendResponseHeaders(404, -1);
        } else {
            String response = document.toJson();
            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.getResponseBody().close();
        }
    }

    //DELETE /birds/{id}
    private void httpDelete(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();
        String birdId = path.substring(7);
        if (birdId.length() == 0) {
            httpExchange.sendResponseHeaders(404, -1);
            return;
        }

        birdDocument.put("name", birdId);
        DeleteResult deleteResult = mongoCollection.deleteOne(birdDocument);
        if (deleteResult.getDeletedCount() >= 1) {
            httpExchange.sendResponseHeaders(200, -1);
        } else {
            httpExchange.sendResponseHeaders(404, -1);
        }
    }

    //POST /birds
    private void httpPost(HttpExchange httpExchange) throws IOException {

        try (InputStream request = httpExchange.getRequestBody();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            Document document = Document.parse(stringBuilder.toString());

            if (document.get("name") == null || document.get("family") == null || document.get("continents") == null) {
                httpExchange.sendResponseHeaders(400, -1);
                return;
            }

            Object continents = document.get("continents");
            if (continents instanceof ArrayList) {
                if (((ArrayList) continents).size() < 1) {
                    httpExchange.sendResponseHeaders(400, -1);
                    return;
                }
            }

            if (document.get("added") == null) {
                document.put("added", simpleDateFormat.format(new Date()));
            }

            if (document.get("visible") == null) {
                document.put("visible", false);
            }

            try {
                mongoCollection.insertOne(document);
                httpExchange.sendResponseHeaders(201, 0);
                String response = document.toJson();
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.getResponseBody().close();
            } catch (MongoException mwe) {
                httpExchange.sendResponseHeaders(400, -1);
            }
        }
    }
}
