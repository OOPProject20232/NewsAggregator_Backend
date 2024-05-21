package newsaggregator;

import com.sun.net.httpserver.HttpServer;
import newsaggregator.cloud.ServerController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * Lớp này dùng để khởi tạo server và xử lý request từ client
 * <br> gồm 3 endpoints:
 * <br> - /v1/articles
 * <br> - /v1/posts
 * <br> - /v1/coins
 * <br> Khi người dùng chạy, chương trình sẽ ở port 8000
 * <br> => nếu người dùng muốn crawl dữ liệu bài báo (article) thì sẽ gửi request GET tới <a href="http://localhost:8000/v1/articles">http://localhost:8000/v1/articles</a>
 * (mở browser và nhập link)
 * <br> Server trả về response 202 (Accepted) và thực hiện crawl dữ liệu bài báo, sau khi crawl xong sẽ trả về response 200 (OK) và trả về dữ liệu đã crawl được
 * Khi crawl thành công, dữ liệu sẽ được đẩy lên MongoDB
 * <br> Ngược lại, trả về 500 (Internal Server Error) nếu có lỗi xảy ra
 *
 * @see ServerController
 * @see HttpServer
 */
public class Main {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        server.createContext("/v1/articles", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(202, 0);
                Future<String> future = executor.submit(ServerController::runArticles);
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
                Future<String> future = executor.submit(ServerController::runPosts);
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
                Future<String> future = executor.submit(ServerController::runCoins);
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
