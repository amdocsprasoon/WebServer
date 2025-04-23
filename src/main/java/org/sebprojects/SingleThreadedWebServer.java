package org.sebprojects;

import java.io.*;
import java.net.*;

public class SingleThreadedWebServer {
    public static void main(String[] args) {
        int port = 8080; // Port to listen on
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("New client connected");

                    // Read the HTTP request
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line = in.readLine();
                    if (line == null || line.isEmpty()) {
                        continue;
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
//                    StringBuilder body = new StringBuilder();
//                    if ("POST".equalsIgnoreCase(method)) {
//                        while (in.ready()) {
//                            body.append((char) in.read());
//                        }
//                        System.out.println("Body: " + body);
//                    }

                    // Write the HTTP response
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
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
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}