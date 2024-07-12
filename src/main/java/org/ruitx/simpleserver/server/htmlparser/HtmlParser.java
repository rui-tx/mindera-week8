package org.ruitx.simpleserver.server.htmlparser;

import org.ruitx.simpleserver.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HtmlParser {

    /**
     * Parse the HTML file and replace the placeholders with the actual values
     *
     * @param htmlPage
     * @return the parsed HTML
     * @throws IOException
     */

    public static String parseHTML(File htmlPage) throws IOException {

        String html = new String(Files.readAllBytes(Path.of(htmlPage.getPath())));

        String regex = "\\{\\{([^}]*)}}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String command = matcher.group(1).trim();
            if (command.startsWith("pathFor")) {
                String file = command.replace("pathFor", "").trim();

                String cmdRegex = "\\(\"([^\"]+)\"\\)";
                Matcher cmdMatcher = Pattern.compile(cmdRegex).matcher(file);
                while (cmdMatcher.find()) {
                    file = cmdMatcher.group(1);
                }
                matcher.appendReplacement(result, Constants.URL + file);
            }

            if (command.startsWith("getServerPort()")) {
                String file = command.replace("getServerPort()", "").trim();
                matcher.appendReplacement(result, "Server port is: " + Constants.DEFAULT_PORT);
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
