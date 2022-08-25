package com.company;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
	// write your code here
        try {
            HttpServer server = makeServer();
            server.start();
            initRoutes(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost"; // 127.0.0.1
        InetSocketAddress address = new InetSocketAddress(host, 4444);

        System.out.printf("We start the server at : http://%s:%s%n",
                address.getHostName(), address.getPort());

        HttpServer server = HttpServer.create(address, 20);
        System.out.println("Successfully connected!");

        return server;
    }
    
    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleRootRequest);
    }
    
    private static void handleRootRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plane; charset = utf-8");
            
            int response = 200;
            int length = 0;
            exchange.sendResponseHeaders(length, response);
            
            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String ctxPath = exchange.getHttpContext().getPath();
                
                write(writer, "HTTP method", method);
                write(writer, "Zapros", uri.toString());
                write(writer, "Obrabotan 4erez", ctxPath);
                writeHeaders(writer, "Zagolovki zaprosa", exchange.getRequestHeaders());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, " ");
        headers.forEach((k, v) -> write(writer, "\t" + k,v.toString()));
    }

    private static void write(Writer writer, String msg, String method) {
        String date = String.format("%s:%s%n%n", msg, method);

        try {
            writer.write(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output, false, charset);
    }
}
