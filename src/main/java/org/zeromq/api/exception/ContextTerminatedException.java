package org.zeromq.api.exception;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 * Thrown to indicate the context has been terminated. This exception directly
 * maps to the <code>ETERM</code> error code in libzmq.
 * 
 * @author sjohnr
 */
public class ContextTerminatedException extends ZMQRuntimeException {
    private static final long serialVersionUID = -3914068543257736644L;

    /**
     * Constructor, with ZMQException cause.
     * 
     * @param message The error message
     * @param cause The underlying cause
     */
    public ContextTerminatedException(String message, ZMQException cause) {
        super(message, (int) ZMQ.Error.ETERM.getCode());
        initCause(cause);
    }

    /**
     * Constructor, with ZMQException cause.
     * 
     * @param message The error message
     * @param cause The underlying cause
     */
    public ContextTerminatedException(ZMQException cause) {
        this(cause.getMessage(), cause);
    }
}
