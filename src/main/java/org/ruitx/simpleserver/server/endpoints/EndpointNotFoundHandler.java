package org.ruitx.simpleserver.server.endpoints;


import org.ruitx.simpleserver.Constants;
import org.ruitx.simpleserver.server.Header;
import org.ruitx.simpleserver.server.Messages;
import org.ruitx.simpleserver.server.ResponseCodes;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class EndpointNotFoundHandler implements EndpointHandler {

    @Override
    public void execute(DataOutputStream out) throws IOException {
        File htmlFile = new File(Constants.RESOURCES_PATH + "404.html");
        Header responseHeader = new Header.Builder(ResponseCodes.NOT_FOUND.toString())
                .contentType(Files.probeContentType(Path.of(htmlFile.getPath())))
                .contentLength(String.valueOf(htmlFile.length()))
                .endResponse()
                .build();

        out.write(responseHeader.headerToBytes());
        out.write(Files.readAllBytes(Path.of(htmlFile.getPath())), 0, Files.readAllBytes(Path.of(htmlFile.getPath())).length);

        System.out.printf(Messages.SERVER_LOG.getMessage(),
                Instant.now().getEpochSecond(),
                Messages.ENDPOINT_NOT_FOUND);
    }
}
