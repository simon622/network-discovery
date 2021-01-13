package org.slj.network.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class NetworkDiscoveryAgentUtils {

    public static ByteBuffer wrap(byte[] arr){
        return wrap(arr, arr.length);
    }

    public static ByteBuffer wrap(byte[] arr, int length){
        return ByteBuffer.wrap(arr, 0 , length);
    }

    public static byte[] drain(ByteBuffer buffer){
        byte[] arr = new byte[buffer.remaining()];
        buffer.get(arr, 0, arr.length);
        return arr;
    }

    public static InetAddress deriveSourceFromNetworkInterface(String address, int port) throws IOException {
        try (Socket socket = new Socket()){
            socket.setSoTimeout(1000);
            socket.connect(new InetSocketAddress(address, port));
            return socket.getLocalAddress();
        }
    }
}
