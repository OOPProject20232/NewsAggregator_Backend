package newsaggregator;

import com.sun.net.httpserver.HttpServer;
import newsaggregator.cloud.App;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/v1/articles", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = App.runArticles();
                exchange.sendResponseHeaders(200, 0);
                byte[] responseBytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8"); // Set content type
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        }));
        server.createContext("/v1/posts", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = App.runPosts();
                exchange.sendResponseHeaders(200, 0);
                byte[] responseBytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8"); // Set content type
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        }));
        server.createContext("/v1/coins", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = App.runCoins();
                exchange.sendResponseHeaders(200, 0);
                byte[] responseBytes = response.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8"); // Set content type
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        }));
        server.setExecutor(null);
        server.start();
    }
}
