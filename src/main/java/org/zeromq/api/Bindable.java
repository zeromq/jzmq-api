package org.zeromq.api;

/**
 * 
 */
public interface Bindable {
    public Socket bind(String url) throws Exception;
}
