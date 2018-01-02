package com.mobillium.omnicrow.utils;

/**
 * An Exception indicating that the com.mobillium.OmniCrowSdk.OmniCrow SDK has not been correctly initialized.
 */
public class OmniCrowAnalyticsSdkNotInitializedException extends OmniCrowAnalyticsException {
    static final long serialVersionUID = 1;

    /**
     * Constructs a OmniCrowAnalyticsSdkNotInitializedException with no additional information.
     */
    public OmniCrowAnalyticsSdkNotInitializedException() {
        super();
    }

    /**
     * Constructs a OmniCrowAnalyticsSdkNotInitializedException with a message.
     *
     * @param message A String to be returned from getMessage.
     */
    public OmniCrowAnalyticsSdkNotInitializedException(String message) {
        super(message);
    }

    /**
     * Constructs a OmniCrowAnalyticsSdkNotInitializedException with a message and inner error.
     *
     * @param message   A String to be returned from getMessage.
     * @param throwable A Throwable to be returned from getCause.
     */
    public OmniCrowAnalyticsSdkNotInitializedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a OmniCrowAnalyticsSdkNotInitializedException with an inner error.
     *
     * @param throwable A Throwable to be returned from getCause.
     */
    public OmniCrowAnalyticsSdkNotInitializedException(Throwable throwable) {
        super(throwable);
    }
}
