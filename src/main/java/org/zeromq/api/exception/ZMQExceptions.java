package org.zeromq.api.exception;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 * Helper class for handling exceptions, especially with jzmq.
 */
public class ZMQExceptions {

    private ZMQExceptions() {}

    /**
     * Wrap an underlying ZMQException in the appropriate higher level exception.
     * 
     * @param thrown The underlying ZMQException
     * @return A new exception, which wraps a ZMQException
     */
    public static ZMQRuntimeException wrap(ZMQException thrown) {
        if (isContextTerminated(thrown)) {
            return new ContextTerminatedException(thrown);
        } else if (isInvalidSocket(thrown)) {
            return new InvalidSocketException(thrown);
        } else {
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
        ZMQ.Error error = ZMQ.Error.findByCode(thrown.getErrorCode());
        return error.name().equals("ETERM");
    }

    /**
     * Helper method to determine if error code is <code>ENOTSOCK</code>.
     * 
     * @param thrown A ZMQException, thrown by the library
     * @return true if the exception indicates ENOTSOCK, false otherwise
     */
    public static boolean isInvalidSocket(ZMQException thrown) {
        ZMQ.Error error = ZMQ.Error.findByCode(thrown.getErrorCode());
        return error.name().equals("ENOTSOCK");
    }
}
