package org.ruitx.simpleserver;

public enum Messages {
    SERVER_STARTED("Server started on port %d\n"),
    CLIENT_CONNECTED("[CLIENT] Connected:  %s:%d\n"),
    CLIENT_DISCONNECTED("[CLIENT] Disconnected: %s:%d\n");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public static void print(Messages message) {
        System.out.printf(message.getMessage());
    }

    public String getMessage() {
        return message;
    }
}
