package org.zeromq.api.exception;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

public class ZMQExceptions {

    private ZMQExceptions() {}

    /**
     * Wrap an underlying ZMQException in the appropriate higher level exception.
     * 
     * @param thrown The underlying ZMQException
     * @return A new exception, which wraps a ZMQException
     */
    public static ZMQRuntimeException wrap(ZMQException thrown) {
        switch (ZMQ.Error.findByCode(thrown.getErrorCode())) {
            case ETERM:
                return new ContextTerminatedException(thrown);
            case ENOTSOCK:
                return new InvalidSocketException(thrown);
            default:
                return new ZMQRuntimeException(thrown);
        }
    }

    /**
     * Helper method to determine if error code is <code>ETERM</code>.
     * 
     * @param thrown A ZMQException, thrown by the library
     * @return true if the exception indicates ETERM, false otherwise
     */
    public static boolean isContextTerminated(ZMQException thrown) {
        return (ZMQ.Error.ETERM.getCode() == thrown.getErrorCode());
    }

    /**
     * Helper method to determine if error code is <code>ENOTSOCK</code>.
     * 
     * @param thrown A ZMQException, thrown by the library
     * @return true if the exception indicates ENOTSOCK, false otherwise
     */
    public static boolean isInvalidSocket(ZMQException thrown) {
        return (ZMQ.Error.ENOTSOCK.getCode() == thrown.getErrorCode());
    }
}
