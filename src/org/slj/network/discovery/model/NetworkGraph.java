package org.slj.network.discovery.model;

import org.slj.network.discovery.NetworkDiscoveryException;

import java.util.*;

public class NetworkGraph {

    private String localHost;

    private final Object monitor = new Object();
    private final Map<String, NetworkNode> network =
            Collections.synchronizedMap(new HashMap<>());

    public NetworkGraph(String localHost){
        this.localHost = localHost;
    }

    public NetworkNode getNode(String name){
        Iterator<String> itr = network.keySet().iterator();
        synchronized (network){
            while(itr.hasNext()){
                String hostName = itr.next();
                NetworkNode host = network.get(hostName);
                if(host.getName().equals(name)){
                    return host;
                }
            }
        }
        return null;
    }

    public List<NetworkNode> getAllNodesByGroupName(String groupName, boolean includeSelf){
        List<NetworkNode> l = new ArrayList<>();
        Iterator<String> itr = network.keySet().iterator();
        synchronized (network){
            while(itr.hasNext()){
                String hostName = itr.next();
                NetworkNode host = network.get(hostName);
                if(!includeSelf && hostName.equals(localHost))
                    continue;

                if(host.getGroup().equals(groupName)){
                    l.add(host);
                }
            }
        }
        return Collections.unmodifiableList(l);
    }

    public List<NetworkNode> getAllHealthyNodes(boolean includeSelf){
        return getAllHealthyNodes(null, includeSelf);
    }

    public List<NetworkNode> getAllHealthyNodes(String groupName, boolean includeSelf){
        List<NetworkNode> l = new ArrayList<>();
        Iterator<String> itr = network.keySet().iterator();
        synchronized (network){
            while(itr.hasNext()){
                String hostName = itr.next();
                NetworkNode host = network.get(hostName);
                if(groupName != null &&
                        !groupName.equals(host.getGroup()))
                    continue;

                if(!includeSelf && hostName.equals(localHost))
                    continue;

                if(host.getStatus() == NetworkNode.HEALTHY){
                    l.add(host);
                }
            }
        }
        return Collections.unmodifiableList(l);
    }

    public NetworkNode waitOnFirstHealthyNode(String groupName, boolean includeSelf, int waitTimeMillis)
            throws NetworkDiscoveryException {

        try {
            long until = System.currentTimeMillis() + waitTimeMillis;
            List<NetworkNode> healthy = null;
            do {
                healthy = getAllHealthyNodes(groupName, includeSelf);
                if(healthy.isEmpty()) {
                    synchronized (monitor) {
                        //ensure spurious wake ups dont cause waits longer than requested
                        monitor.wait(Math.max(1, until - System.currentTimeMillis()));
                    }
                }
            }
            while(healthy.isEmpty() && System.currentTimeMillis() < until);

            if(healthy.isEmpty())
                throw new NetworkDiscoveryException("unable to discover healthy host in group name in ["+waitTimeMillis+"]");
            return healthy.get(0);

        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new NetworkDiscoveryException("wait interrupted");
        }
    }

    public boolean receiveMessage(BroadcastMessage message, boolean processPeers){

        final NetworkNode node = message.getNode();
        boolean updated = update(message.getStatus(), node);
        if(processPeers && message.getPeers() != null){
            Iterator<NetworkNode> itr = message.getPeers().iterator();
            while(itr.hasNext()){
                NetworkNode peerHost = itr.next();
                updated |= update(BroadcastMessage.PING, peerHost);
            }
        }

        return updated;
    }

    private final boolean update(int status, NetworkNode node){

        String name = node.getName();
        NetworkNode oldHost = null;
        boolean update = true;
        if((oldHost = network.get(name)) != null) {
            //-- only replace the old version if the timestamp is later than what we have
            if (oldHost.getEventTime() >
                    node.getEventTime()) {
                update = false;
            }
        }

        if(update){
            switch(status){
                case BroadcastMessage.BIRTH:
                    network.put(name, node);
                    break;
                case BroadcastMessage.DEATH:
                    network.remove(name);
                    break;
                case BroadcastMessage.PING:
                    network.put(name, node);
                    if(node.getStatus() == NetworkNode.HEALTHY){
                        synchronized (monitor){
                            monitor.notifyAll();
                        }
                    }
                    break;
                default: break;
            }
        }

        return update;
    }
}