package org.zeromq.api;

/**
 * Used to create sockets capable of binding to local endpoints.
 */
public interface Bindable {
    /**
     * Bind to a url.
     * 
     * @param url the url to bind to
     * @return the socket
     */
    Socket bind(String url);

    /**
     * Bind to a url.
     * 
     * @param url the url to bind to
     * @param additionalUrls additional urls to bind to
     * @return the socket
     */
    Socket bind(String url, String... additionalUrls);
}
