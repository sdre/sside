import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SaltsideHttpServer {

    public static void main(String[] args) {

        SaltsideHttpServer saltsideHttpServer = new SaltsideHttpServer();
        saltsideHttpServer.createServer();
    }

    private void createServer() {

        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(8081), 0);
            httpServer.createContext("/birds", new BirdHandler());
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
