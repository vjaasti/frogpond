package org.frogpond;

public class LilyException extends RuntimeException {

    public LilyException(Throwable cause) {
        super(cause);
    }

    public LilyException(String message, Throwable cause) {
        super(message, cause);
    }

    public LilyException(String message) {
        super(message);
    }

    public LilyException() {
    }
}
