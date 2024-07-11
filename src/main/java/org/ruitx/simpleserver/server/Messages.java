package org.ruitx.simpleserver.server;

import org.ruitx.simpleserver.utils.TerminalColors;

public enum Messages {
    SERVER_LOG("[SERVER]" + TerminalColors.ANSI_WHITE + "[%d]" + TerminalColors.ANSI_RESET
            + "-> %s\n"),
    SERVER_STARTED(TerminalColors.ANSI_GREEN + "Server started" + TerminalColors.ANSI_RESET + "\n"),
    REQUEST_ERROR("Error: Request not recognized\n"),
    INVALID_REQUEST("Error: Invalid request\n"),
    ENDPOINT_NOT_FOUND("Endpoint not found"),
    INTERNAL_SERVER_ERROR("Error: Internal server error\n"),

    CLIENT_LOG(TerminalColors.ANSI_WHITE + "[%d]" + TerminalColors.ANSI_RESET
            + TerminalColors.ANSI_BLUE + "[%s:%d]" + TerminalColors.ANSI_RESET
            + "-> %s\n"),

    CLIENT_CONNECTED(TerminalColors.ANSI_WHITE + "Connect" + TerminalColors.ANSI_RESET),
    CLIENT_DISCONNECTED(TerminalColors.ANSI_WHITE + "Disconnect" + TerminalColors.ANSI_RESET);

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
