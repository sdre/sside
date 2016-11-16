import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaltsideTester {

    public static void main(String[] args) {

        SaltsideTester tester = new SaltsideTester();
        tester.test();
    }

    private void test() {

        String postURL = "http://localhost:8081/birds";
        String postJson1 = "{\"name\":\"pigeon\", \"family\":\"columbidae\", \"continents\":[\"asia\", \"europe\"]}";
        String postJson2 = "{\"name\":\"crow\", \"family\":\"corvus\", \"continents\":[\"asia\", \"africa\", \"europe\"]}";
        String postJson3 = "{\"name\":\"parrot\", \"family\":\"psittacidae\", \"continents\":[\"asia\"], \"visible\":true}";
        String postJson4 = "{\"name\":\"sparrow\", \"family\":\"passeridae\", \"continents\":[]}";
        String postJson5 = "{\"name\":\"peacock\", \"family\":\"anatidae\", \"continents\":[\"australia\", \"africa\"], \"visible\":true}";
        try {
            httpPost(postURL, postJson1);
            httpPost(postURL, postJson2);
            httpPost(postURL, postJson3);
            httpPost(postURL, postJson4);
            httpPost(postURL, postJson5);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String URL1 = "http://localhost:8081/birds/pigeon";
        String URL2 = "http://localhost:8081/birds/crow";
        String URL3 = "http://localhost:8081/birds/parrot";
        String URL4 = "http://localhost:8081/birds/sparrow";
        String URL5 = "http://localhost:8081/birds/peacock";
        try {
            httpGet(URL1);
            httpGet(URL2);
            httpGet(URL3);
            httpGet(URL4);
            httpGet(URL5);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String getAllURL = "http://localhost:8081/birds/";
        try {
            httpGet(getAllURL);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            httpDelete(URL1);
            httpDelete(URL2);
            httpDelete(URL3);
            httpDelete(URL4);
            httpDelete(URL5);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void httpPost(String url, String json) throws IOException {

        System.out.println("Posting JSON to URL : " + url);
        System.out.println(json);

        URL postUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(json.getBytes());
        }

        System.out.println("Response Code : " + connection.getResponseCode() + " " + connection.getResponseMessage());
        System.out.println(getResponse(connection));

        System.out.println("-----------------------------------");
    }

    private void httpGet(String url) throws IOException {

        System.out.println("Get JSON from URL : " + url);

        URL postUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        System.out.println("Response Code : " + connection.getResponseCode() + " " + connection.getResponseMessage());
        System.out.println(getResponse(connection));

        System.out.println("-----------------------------------");
    }

    private void httpDelete(String url) throws IOException {

        System.out.println("DELETE URL : " + url);

        URL postUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setDoOutput(true);

        System.out.println("Response Code : " + connection.getResponseCode() + " " + connection.getResponseMessage());
        System.out.println(getResponse(connection));

        System.out.println("-----------------------------------");
    }

    private String getResponse(HttpURLConnection connection) {


        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException ioe) {
            return ioe.getMessage();
        }
    }
}
