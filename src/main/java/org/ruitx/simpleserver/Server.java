package org.ruitx.simpleserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public Server() {

    }

    public void start() throws IOException {
        int port = System.getenv("PORT") != null
                ? Integer.parseInt(System.getenv("PORT"))
                : Constants.DEFAULT_PORT;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            try (ExecutorService executorService = Executors.newCachedThreadPool()) {
                System.out.printf(Messages.SERVER_LOG.getMessage(),
                        Instant.now().getEpochSecond(),
                        Messages.SERVER_STARTED.getMessage());
                System.out.printf(Messages.SERVER_LOG.getMessage(),
                        Instant.now().getEpochSecond(),
                        "Using port " + port);
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
            headers = new LinkedHashMap<>();
            in = null;
            out = null;
        }

        @Override
        public void run() {
            try {
                dealWithRequest(socket);
            } catch (IOException e) {
                Messages.print(Messages.INTERNAL_SERVER_ERROR);
                //throw new RuntimeException(e);
            }
        }

        /**
         * Deal with the client request
         *
         * @param socket
         */
        public void dealWithRequest(Socket socket) throws IOException {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());

            // Get request headers into an ordered map
            String requesHeaderLine;
            while (!Objects.equals(requesHeaderLine = in.readLine(), "")) {
                String[] requestLine = requesHeaderLine.split(" ", 2);
                headers.put(requestLine[0].replace(":", ""), requestLine[1]);
            }

            System.out.printf(Messages.CLIENT_LOG.getMessage(),
                    Instant.now().getEpochSecond(),
                    socket.getInetAddress().getHostAddress(),
                    socket.getPort(),
                    Messages.CLIENT_CONNECTED.getMessage());

            checkRequestAndSendResponse(headers);
            this.closeSocket();

            System.out.printf(Messages.CLIENT_LOG.getMessage(),
                    Instant.now().getEpochSecond(),
                    socket.getInetAddress().getHostAddress(),
                    socket.getPort(),
                    Messages.CLIENT_DISCONNECTED.getMessage());
        }

        /**
         * Check the request type and send the appropriate response
         *
         * @param headers
         */
        private void checkRequestAndSendResponse(Map<String, String> headers) throws IOException {
            String requestType = getRequestType(headers);
            sendResponse(requestType);
        }

        /**
         * Get the request type from the request headers
         *
         * @param headers
         * @return
         */
        private String getRequestType(Map<String, String> headers) {
            Optional<String> requestType = headers.keySet()
                    .stream()
                    .filter(key -> key.equals("GET") || key.equals("POST") || key.equals("PUT") || key.equals("PATCH") || key.equals("DELETE"))
                    .findFirst();

            return requestType.orElse("INVALID");
        }

        /**
         * Send the appropriate response based on the request type
         *
         * @param requestType
         */
        private void sendResponse(String requestType) throws IOException {
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

        /**
         * Send the GET request
         *
         * @param endPoint the end point to send
         * @throws IOException
         */
        private void sendGET(String endPoint) throws IOException {
            String htmlFile;
            htmlFile = endPoint.equals("/")
                    ? Constants.RESOURCES_PATH + "index.html"
                    : Constants.RESOURCES_PATH + endPoint;

            File htmlPage = new File(htmlFile);
            if (!htmlPage.exists() || !htmlPage.isFile()) {
                send404();
                return;
            }

            Header responseHeader = new Header.Builder(ResponseCodes.OK.toString())
                    .contentType(Files.probeContentType(Path.of(htmlPage.getPath())))
                    .contentLength(String.valueOf(htmlPage.length()))
                    .endResponse()
                    .build();

            sendHeaderAndPage(responseHeader.headerToBytes(), Files.readAllBytes(Path.of(htmlPage.getPath())));
        }

        private void send404() throws IOException {
            File htmlFile = new File(Constants.RESOURCES_PATH + "404.html");
            Header responseHeader = new Header.Builder(ResponseCodes.NOT_FOUND.toString())
                    .contentType(Files.probeContentType(Path.of(htmlFile.getPath())))
                    .contentLength(String.valueOf(htmlFile.length()))
                    .endResponse()
                    .build();

            sendHeaderAndPage(responseHeader.headerToBytes(), Files.readAllBytes(Path.of(htmlFile.getPath())));
        }

        /**
         * Send the header and the page
         *
         * @param htmlPage the page to send
         * @param header   the header to send
         * @throws IOException
         */
        private void sendHeaderAndPage(byte[] header, byte[] htmlPage) throws IOException {
            out.write(header);
            out.write(htmlPage, 0, htmlPage.length);
        }

        /**
         * Close the socket
         *
         * @throws IOException
         */
        private void closeSocket() throws IOException {
            this.socket.close();
        }
    }
}
