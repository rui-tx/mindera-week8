package org.ruitx.simpleserver.server.endpoints;

public enum Endpoint {
    TEST("/test", new TestHandler()),

    NOT_FOUND("Endpoint not found", new EndpointNotFoundHandler());

    private final String description;
    private final EndpointHandler handler;

    Endpoint(String description, EndpointHandler handler) {
        this.description = description;
        this.handler = handler;
    }

    public static Endpoint getEndpoint(String description) {
        for (Endpoint endpoint : values()) {
            if (description.equals(endpoint.description)) {
                return endpoint;
            }
        }
        return NOT_FOUND;
    }

    public EndpointHandler getHandler() {
        return handler;
    }

    public String getDescription() {
        return description;
    }
}
