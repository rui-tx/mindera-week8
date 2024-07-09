package org.ruitx.simpleserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public Server() {

    }

    public void start() throws IOException {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            try (ExecutorService executorService = Executors.newCachedThreadPool()) {
                System.out.printf(Messages.SERVER_STARTED.getMessage(), port);

                acceptConnections(serverSocket, executorService);
            }
        }
    }

    private void acceptConnections(ServerSocket serverSocket, ExecutorService executorService) {
        while (true) {
            try {
                RequestHandler requestHandler = new RequestHandler(serverSocket.accept());

                executorService.submit(requestHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class RequestHandler implements Runnable {

        private final Socket socket;
        private final Map<String, String> headers;
        private BufferedReader in;
        private DataOutputStream out;

        public RequestHandler(Socket socket) {

            this.socket = socket;
            headers = new HashMap<>();
            in = null;
            out = null;
        }

        @Override
        public void run() {
            dealWithRequest(socket);
        }

        public void dealWithRequest(Socket socket) {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());

                String requesHeaderLine = "";
                while (!Objects.equals(requesHeaderLine = in.readLine(), "")) {
                    String[] requestLine = requesHeaderLine.split(" ", 2);
                    headers.put(requestLine[0], requestLine[1]);
                }

                checkRequest(headers);

                this.closeSocket();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //System.out.printf(Messages.CLIENT_CONNECTED.getMessage(), socket.getInetAddress().getHostAddress(), socket.getPort());
        }

        private void checkRequest(Map<String, String> headers) {
            String requestType = getRequestType(headers);
            sendResponse(requestType);
        }

        private String getRequestType(Map<String, String> headers) {
            Optional<String> requestType = headers.keySet()
                    .stream()
                    .filter(key -> key.equals("GET") || key.equals("POST") || key.equals("PUT") || key.equals("PATCH") || key.equals("DELETE"))
                    .findFirst();

            return requestType.orElse("INVALID");
        }

        private void sendResponse(String requestType) {
            RequestType request = RequestType.fromString(requestType);

            switch (request) {
                case GET:
                    for (String key : headers.keySet()) {
                        if (key.equals("GET")) {
                            String endPoint = headers.get(key).split(" ")[0];
                            sendGET(endPoint);
                            return;
                        }
                    }
                    break;
                case POST:
                    System.out.println("POST");
                    break;
                case PUT:
                    System.out.println("PUT");
                    break;
                case PATCH:
                    System.out.println("PATCH");
                    break;
                case DELETE:
                    System.out.println("DELETE");
                    break;
                default:
                    System.out.println("INVALID");
                    break;
            }
        }

        private void send404() {
            String path = "/Users/ruiteixeira/NC/Coding/mindera/mindera-week8/";  //TODO: Change to real path
            File file = new File(path + "src/main/resources/simpleserver/404.html");

            try {
                byte[] bytes = Files.readAllBytes(Path.of(file.getPath()));
                out.writeBytes(
                        "HTTP/1.1 404 NOT FOUND\r\n" +
                                "Content-Type: " + Files.probeContentType(Path.of(file.getPath())) + "\r\n" +
                                "Content-Length: " + file.length() + "\r\n" +
                                "\r\n");
                out.write(bytes, 0, bytes.length);
                return;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void sendGET(String endPoint) {
            String path = "/Users/ruiteixeira/NC/Coding/mindera/mindera-week8/";  //TODO: Change to real path
            String filePath = "";
            if (endPoint.equals("/")) {
                filePath = path + "src/main/resources/simpleserver/index.html";
            } else {
                filePath = path + "src/main/resources/simpleserver/" + endPoint;
            }

            File file = new File(filePath);

            if (!file.exists() || !file.isFile()) {
                send404();
                return;
            }

            try {
                byte[] bytes = Files.readAllBytes(Path.of(file.getPath()));
                out.writeBytes(
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + Files.probeContentType(Path.of(filePath)) + "\r\n" +
                                "Content-Length: " + file.length() + "\r\n" +
                                "\r\n");
                out.write(bytes, 0, bytes.length);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        private void closeSocket() {
            try {
                //System.out.printf(Messages.CLIENT_DISCONNECTED.getMessage(), socket.getInetAddress().getHostAddress(), socket.getPort());
                this.socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


                /*
                try {
                    requestLine = in.readLine();
                    if (requestLine.equals("GET / HTTP/1.1")) {
                        out.writeBytes("HTTP/1.1 200 OK\r\n\r\n");

                        String htmlFile = """
                                <!DOCTYPE html>
                                <html>
                                    <head>
                                        <title>Hello World</title>
                                    </head>
                                    <body>
                                        <h1>Hello World</h1>
                                    </body>
                                </html>
                                """;


                        byte[] bytes = htmlFile.getBytes();
                        out.write(bytes, 0, bytes.length);
                    }

                    if (requestLine.equals("GET /favicon.ico HTTP/1.1")) {
                        out.writeBytes("HTTP/1.1 200 OK\r\n\r\n");

                        File file = new File("favicon.ico");
                        byte[] bytes = Files.readAllBytes(Path.of(file.getPath()));
                        out.write(bytes, 0, bytes.length);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                 */

                /*while (!Objects.equals(requestLine = in.readLine(), "")) {
                    if (requestLine.equals("GET / HTTP/1.1")) {
                        out.writeBytes("HTTP/1.1 200 OK\r\n");
                        byte[] bytes = "1234".getBytes();
                        out.write(bytes, 0, bytes.length);
                    }

                    System.out.println(requestLine);
                 */