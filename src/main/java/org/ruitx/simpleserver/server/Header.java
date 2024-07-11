package org.ruitx.simpleserver.server;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Header {
    private final String requestType;
    private final String contentType;
    private final String contentLength;
    private final String endResponse;

    private Header(Builder builder) {
        this.requestType = builder.response;
        this.contentType = builder.contentType;
        this.contentLength = builder.contentLength;
        this.endResponse = builder.endResponse;
    }

    /**
     * Convert the header to a string
     * Checks if the field is not null before converting it to a string
     *
     * @return the header as a string
     */
    public String headerToString() {
        Field[] fields = this.getClass().getDeclaredFields();
        List<Field> nonNullFields = Arrays.stream(fields)
                .filter(f -> {
                    try {
                        return f.get(this) != null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return nonNullFields.stream()
                .map(f -> {
                    try {
                        return "" + f.get(this);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.joining());
    }

    public byte[] headerToBytes() {
        return headerToString().getBytes();
    }

    public static class Builder {
        private final String response;
        private String endResponse;
        private String contentType;
        private String contentLength;

        public Builder(String response) {
            if (response == null) {
                throw new IllegalArgumentException("Request type cannot be null");
            }
            this.response = "HTTP/1.1 " + response + "\r\n";
        }

        public Builder contentType(String contentType) {
            this.contentType = "Content-Type: " + contentType + "\r\n";
            return this;
        }

        public Builder contentLength(String contentLength) {
            this.contentLength = "Content-Length: " + contentLength + "\r\n";
            return this;
        }

        public Builder endResponse() {
            this.endResponse = "\r\n";
            return this;
        }

        public Header build() {
            return new Header(this);
        }
    }
}
