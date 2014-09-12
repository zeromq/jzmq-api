package org.zeromq.api.exception;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 * Thrown to indicate an invalid socket. This exception directly maps to the
 * <code>ENOTSOCK</code> error code in libzmq.
 * 
 * @author sjohnr
 */
public class InvalidSocketException extends ZMQRuntimeException {
    private static final long serialVersionUID = 4376146106003013395L;

    /**
     * Constructor, with ZMQException cause.
     * 
     * @param message The error message
     * @param cause The underlying cause
     */
    public InvalidSocketException(String message, ZMQException cause) {
        super(message, (int) ZMQ.Error.ENOTSOCK.getCode());
        initCause(cause);
    }

    /**
     * Constructor, with ZMQException cause.
     * 
     * @param message The error message
     * @param cause The underlying cause
     */
    public InvalidSocketException(ZMQException cause) {
        this(cause.getMessage(), cause);
    }
}
