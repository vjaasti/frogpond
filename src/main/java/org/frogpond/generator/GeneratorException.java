package org.frogpond.generator;

public class GeneratorException extends RuntimeException {
    public GeneratorException() {
    }

    public GeneratorException(String s) {
        super(s);
    }

    public GeneratorException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public GeneratorException(Throwable throwable) {
        super(throwable);
    }
}
