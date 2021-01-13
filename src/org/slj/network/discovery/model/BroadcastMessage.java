package org.slj.network.discovery.model;

import java.io.Serializable;
import java.util.List;

public class BroadcastMessage implements Serializable {

    private static final long serialVersionUID = -973842854132881370L;

    public final static byte
            BIRTH = 0,
            DEATH = 2,
            PING = 4;

    private byte status;
    private NetworkNode node;
    private List<NetworkNode> peers;

    public BroadcastMessage() {
    }

    public BroadcastMessage(NetworkNode node) {
        this.node = node;
    }

    public NetworkNode getNode() {
        return node;
    }

    public void setHost(NetworkNode node) {
        this.node = node;
    }

    public List<NetworkNode> getPeers() {
        return peers;
    }

    public void setPeers(List<NetworkNode> peers) {
        this.peers = peers;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public static String statusToString(int status){
        if(status == BIRTH) return "BIRTH";
        else if(status == DEATH) return "DEATH";
        else if(status == PING) return "PING";
        return "UNKNOWN";
    }

    @Override
    public String toString() {
        return "BroadcastMessage{" +
                "status=" + statusToString(status) +
                ", node=" + node +
                ", peers=" + peers +
                '}';
    }
}
