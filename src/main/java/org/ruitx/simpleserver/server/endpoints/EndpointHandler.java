package org.ruitx.simpleserver.server.endpoints;

import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface EndpointHandler {
    void execute(DataOutputStream out) throws IOException;
}
