package com.jakeapp.core.services.exceptions;

/**
 * We don't know this protocol. Use another one.
 * 
 * @author johannes
 */
@SuppressWarnings("serial")
public class ProtocolNotSupportedException extends Exception {

    public ProtocolNotSupportedException() {
    }

    public ProtocolNotSupportedException(String message) {
        super(message);
    }

    public ProtocolNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolNotSupportedException(Throwable cause) {
        super(cause);
    }
}
