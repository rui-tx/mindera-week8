package org.ruitx.simpleserver.server.endpoints;

import org.ruitx.simpleserver.server.Header;
import org.ruitx.simpleserver.server.ResponseCodes;

import java.io.DataOutputStream;
import java.io.IOException;

public class TestHandler implements EndpointHandler {

    @Override
    public void execute(DataOutputStream out) throws IOException {

        String htmlFragment = """
                <p> === HTML Fragment === </p>
                <p> This text is from the 'test' endpoint </p>
                <p> Click <a href="/index.html">here</a> to go to index </p>
                """;

        Header responseHeader = new Header.Builder(ResponseCodes.OK.toString())
                .contentType("text/html")
                .contentLength(htmlFragment.length() + "")
                .endResponse()
                .build();

        out.write(responseHeader.headerToBytes());
        out.write(htmlFragment.getBytes(), 0, htmlFragment.getBytes().length);
    }
}
