package org.slj.network.discovery;

public class NetworkDiscoveryException extends Exception {

    public NetworkDiscoveryException() {
    }

    public NetworkDiscoveryException(String message) {
        super(message);
    }

    public NetworkDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkDiscoveryException(Throwable cause) {
        super(cause);
    }
}
