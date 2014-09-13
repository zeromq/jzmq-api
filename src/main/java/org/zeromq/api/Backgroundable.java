package org.zeromq.api;

/**
 * Capable of running in a background thread.
 */
public interface Backgroundable {
    /**
     * Run a background thread communicating over the given socket.
     * 
     * @param context The parent context
     * @param socket The socket used for communication
     * @param args Optional arguments
     */
    void run(Context context, Socket socket, Object... args);

    /**
     * Called just prior to the socket being closed.
     */
    void onClose();
}
