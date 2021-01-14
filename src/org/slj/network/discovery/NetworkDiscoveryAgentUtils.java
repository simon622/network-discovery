package org.slj.network.discovery;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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

    private static SecretKeySpec AES_generateKey(String keyStr) throws NoSuchAlgorithmException {
        byte[] key = keyStr.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, "AES");
    }

    public static byte[] AES_encrypt(String secret, byte[] data)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec s = AES_generateKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, s);
        return cipher.doFinal(data);
    }

    public static byte[] AES_decrypt(String secret, byte[] data)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec s = AES_generateKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, s);
        return cipher.doFinal(data);
    }
}