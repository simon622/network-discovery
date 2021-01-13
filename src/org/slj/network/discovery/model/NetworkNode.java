package org.slj.network.discovery.model;

import java.io.Serializable;

public class NetworkNode implements Serializable {

    private static final long serialVersionUID = -9097404222887179043L;

    public static final int
            HEALTHY = 0,
            UNHEALTHY = 2,
            SCALING_IN = 4,
            SCALING_OUT = 8;

    private String name;
    private String group;
    private int status;
    private String address;
    private int port;
    private long eventTime;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkNode host = (NetworkNode) o;

        if (!name.equals(host.name)) return false;
        return group != null ? group.equals(host.group) : host.group == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }

    public static String statusToString(int status){
        if(status == HEALTHY) return "HEALTHY";
        else if(status == UNHEALTHY) return "UNHEALTHY";
        else if(status == SCALING_IN) return "SCALING_IN";
        else if(status == SCALING_OUT) return "SCALING_OUT";
        return "UNKNOWN";
    }

    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", status=" + statusToString(status) +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", eventTime=" + eventTime +
                '}';
    }
}
