package org.zeromq.api.exception;

import org.zeromq.ZMQException;

/**
 * General runtime exception, with underlying error code.
 * 
 * @author sjohnr
 */
public class ZMQRuntimeException extends ZMQException {
    private static final long serialVersionUID = 2581860056724970418L;

    /**
     * Constructor, with ZMQException cause.
     * 
     * @param message The error message
     * @param errorCode The original error code
     */
    public ZMQRuntimeException(String message, int errorCode) {
        super(message, errorCode);
    }

    /**
     * Constructor, with ZMQException cause.
     * 
     * @param message The error message
     * @param cause The underlying cause
     */
    public ZMQRuntimeException(String message, ZMQException cause) {
        super(message, cause.getErrorCode());
        initCause(cause);
    }
}
