package org.ruitx.simpleserver.server.endpoints;

import org.ruitx.simpleserver.server.Header;
import org.ruitx.simpleserver.server.ResponseCodes;

import java.io.DataOutputStream;
import java.io.IOException;

public class TestRevertHandler implements EndpointHandler {

    @Override
    public void execute(DataOutputStream out) throws IOException {

        String htmlFragment = """
                <button hx-get="/test" hx-swap="outerHTML">
                        Click Me
                </button>
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
