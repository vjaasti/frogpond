package org.frogpond;

import org.frogpond.LilyException;

public class SearchException extends LilyException {

    public SearchException() {
    }

    public SearchException(String s) {
        super(s);
    }

    public SearchException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SearchException(Throwable throwable) {
        super(throwable);
    }
}
