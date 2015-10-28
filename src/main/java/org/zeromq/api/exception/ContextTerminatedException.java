package org.zeromq.api.exception;

import org.zeromq.ZMQException;

/**
 * Thrown to indicate the context has been terminated. This exception directly
 * maps to the <code>ETERM</code> error code in libzmq.
 * 
 * @author sjohnr
 */
public class ContextTerminatedException extends ZMQRuntimeException {
    private static final long serialVersionUID = 7996414013283268950L;

    /**
     * Constructor, with ZMQException cause.
     * 
     * @param message The error message
     * @param cause The underlying cause
     */
    public ContextTerminatedException(String message, ZMQException cause) {
        super(message, cause);
    }

    /**
     * Constructor, with ZMQException cause.
     * 
     * @param cause The underlying cause
     */
    public ContextTerminatedException(ZMQException cause) {
        super(cause);
    }
}
