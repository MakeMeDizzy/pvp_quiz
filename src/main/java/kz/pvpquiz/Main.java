package kz.pvpquiz;

import module jdk.httpserver;
import module java.base;

public class Main {

    void main() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        httpServer.createContext("/", exchange -> {
            try (exchange) {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();

                if ("GET".equals(method) && "/health".equals(path)) {
                    String text = "{\"status\":\"ok\"}";
                    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    exchange.getResponseBody().write(bytes);
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            }
        });
        httpServer.start();
        IO.println("Server successfully started");
        Runtime.getRuntime().addShutdownHook(Thread.ofPlatform().name("shutdown-hook").unstarted(
                () -> {
                    IO.println("Stopping server...");
                    httpServer.stop(2);
                    IO.println("Server stopped");
                }
        ));
    }
}
