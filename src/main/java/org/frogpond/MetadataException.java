package org.frogpond;

@SuppressWarnings("serial")
public class MetadataException extends Exception {

    public MetadataException() {
        super();
    }

    public MetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetadataException(String message) {
        super(message);
    }

    public MetadataException(Throwable cause) {
        super(cause);
    }
}
