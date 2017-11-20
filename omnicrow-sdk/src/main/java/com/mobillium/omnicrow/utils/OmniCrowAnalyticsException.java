package com.mobillium.omnicrow.utils;

/**
 * Represents an error condition specific to the Papara SDK for Android.
 */
public class OmniCrowAnalyticsException extends RuntimeException {
    static final long serialVersionUID = 1;

    /**
     * Constructs a new PaparaException.
     */
    public OmniCrowAnalyticsException() {
        super();
    }

    /**
     * Constructs a new PaparaException.
     *
     * @param message the detail message of this exception
     */
    public OmniCrowAnalyticsException(String message) {
        super(message);
    }

    /**
     * Constructs a new PaparaException.
     *
     * @param format the format string (see {@link java.util.Formatter#format})
     * @param args   the list of arguments passed to the formatter.
     */
    public OmniCrowAnalyticsException(String format, Object... args) {
        this(String.format(format, args));
    }

    /**
     * Constructs a new PaparaException.
     *
     * @param message   the detail message of this exception
     * @param throwable the cause of this exception
     */
    public OmniCrowAnalyticsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new PaparaException.
     *
     * @param throwable the cause of this exception
     */
    public OmniCrowAnalyticsException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String toString() {
        // Throwable.toString() returns "PaparaException:{message}". Returning just "{message}"
        // should be fine here.
        return getMessage();
    }
}
