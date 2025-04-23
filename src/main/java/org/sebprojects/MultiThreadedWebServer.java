package org.sebprojects;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedWebServer {
    public static void main(String[] args) {
        int port = 8080; // Port to listen on
        ExecutorService threadPool = Executors.newFixedThreadPool(10); // Thread pool with 10 threads

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                threadPool.execute(new ClientHandler(socket)); // Handle client in a new thread
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            threadPool.shutdown(); // Shut down the thread pool when the server stops
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream())) {

            // Read the HTTP request
            String line = in.readLine();
            if (line == null || line.isEmpty()) {
                return;
            }

            // Parse the request line
            String[] requestLine = line.split(" ");
            String method = requestLine[0];
            String path = requestLine[1];
            System.out.println("Method: " + method + ", Path: " + path);

            // Read headers
            while (!(line = in.readLine()).isEmpty()) {
                System.out.println(line);
            }

            // Handle POST request body
            StringBuilder body = new StringBuilder();
            if ("POST".equalsIgnoreCase(method)) {
                while (in.ready()) {
                    body.append((char) in.read());
                }
                System.out.println("Body: " + body);
            }

            // Write the HTTP response
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Connection: close");
            out.println();
            out.println("<html><body><h1>Request Received</h1></body></html>");
            out.flush();
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }
}