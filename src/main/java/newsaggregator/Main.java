package newsaggregator;

import com.sun.net.httpserver.HttpServer;
import newsaggregator.cloud.App;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        server.createContext("/v1/articles", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(202, 0);
                Future<String> future = executor.submit(App::runArticles);
                if (future.isDone()) {
                    try {
                        String response = future.get();
                        byte[] responseBytes = response.getBytes();
                        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(responseBytes);
                        os.close();
                    } catch (InterruptedException | ExecutionException e) {
                        exchange.sendResponseHeaders(500, 0);
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        }));

        server.createContext("/v1/posts", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(202, 0);
                Future<String> future = executor.submit(App::runPosts);
                if (future.isDone()) {
                    try {
                        String response = future.get();
                        byte[] responseBytes = response.getBytes();
                        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(responseBytes);
                        os.close();
                    } catch (InterruptedException | ExecutionException e) {
                        exchange.sendResponseHeaders(500, 0);
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        }));

        server.createContext("/v1/coins", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(202, 0);
                Future<String> future = executor.submit(App::runCoins);
                if (future.isDone()) {
                    try {
                        String response = future.get();
                        byte[] responseBytes = response.getBytes();
                        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(responseBytes);
                        os.close();
                    } catch (InterruptedException | ExecutionException e) {
                        exchange.sendResponseHeaders(500, 0);
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        }));

        System.out.println("Starting server...");
        server.start();
    }
}
