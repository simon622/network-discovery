package org.slj.network.discovery;

import org.slj.network.discovery.model.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use a network discovery agent in your runtime to monitor and synchronize node information across your network.
 * You can choose to run the agent with the various modes enabled.
 *
 * Network Mode (read mode);
 * Will maintain a {@link NetworkGraph} of nodes on your infrastructure which can be queried in real-time to determine
 * the status of the various network nodes.
 *
 * Broadcast Mode (write mode);
 * Will broadcast local node status information to the network allowing other nodes to know about you.
 * When enabled, broadcast will send BIRTH, PING & DEATH messages to the network. Ping messages
 * will be sent at the interval specified by the config {@link NetworkDiscoveryOptions#getBroadcastIntervalMillis()}.
 * Your runtime can set your local node into HEALTHY or UNHEALTHY states which will cascade across the
 * network to inform other nodes of your availability.
 *
 * Stopping the agent OR using the shutdown hook feature will send DEATH messages across the network removing
 * your node from other nodes availability.
 *
 * Default Mode (both read & write);
 * Both the above modes are active
 */
public class NetworkDiscoveryAgent {

    private Logger logger = Logger.getLogger(NetworkDiscoveryAgent.class.getName());

    private NetworkDiscoveryOptions options;
    private Object monitor = new Object();
    private volatile boolean running = false;
    private Thread networkThread = null;
    private Thread broadcastThread = null;
    private DatagramSocket networkSocket;

    private final String groupName, nodeName;
    private volatile int currentStatus;
    private volatile int port;
    private volatile String hostAddress;
    private Level level;

    private NetworkGraph graph;

    /**
     * Construct a new agent specifying the group name within which, the current host resides and
     * the current host name.
     *
     * NB: Using this constructor will mean broadcast messages FROM this host will attempt to determine
     * the outbound address using a Socket to a public DNS address which is configured in options.
     *
     * @param groupName - A group represents a logical "grouping" of hosts, often referred to as a cluster.
     * @param nodeName - MANDATORY, The unique name of the current host, which can be used to visually identify the host on the network.
     */
    public NetworkDiscoveryAgent(String groupName, String nodeName){
        this.nodeName = nodeName;
        if(nodeName == null){
            throw new IllegalArgumentException("unable to start agent with <null> hostName");
        }
        this.groupName = groupName != null ? groupName.trim() : groupName;
    }

    /**
     * Construct a new agent specifying the group name within which, the current host resides and
     * the current host name.
     *
     * NB: This will use the supplied hostAddress and port in its own broadcast messages rather than
     * attempting a lookup
     *
     * @param groupName - A group represents a logical "grouping" of hosts, often referred to as a cluster.
     * @param nodeName - MANDATORY, The unique name of the current host, which can be used to visually identify the host on the network.
     * @param hostAddress - The local hostAddress to send in broadcast messages
     * @param port - The local port to send in broadcast messages
     */
    public NetworkDiscoveryAgent(String groupName, String nodeName, String hostAddress, int port){
        this(groupName, nodeName);
        this.hostAddress = hostAddress;
        this.port = port;
    }

    /**
     * Start the agent using the supplied configuration.
     * @param options - the config with which to start your agent.
     * @return - a network graph which will be kept synchronized with networking events recieved.
     * @throws NetworkDiscoveryException - an error occurred starting your agent
     */
    public NetworkGraph start(NetworkDiscoveryOptions options) throws NetworkDiscoveryException {
        try {
            this.options = options;
            level = options.isVerboseLoggingEnabled() ? Level.INFO : Level.FINE;
            graph = new NetworkGraph(nodeName);
            if(hostAddress == null){
                deriveLocalAddress();
            }
            running = true;
            if(options.isNetworkEnabled()){
                startNetworkAgent();
            }
            if(options.isBroadcastEnabled()){
                startBroadcastAgent();
                Thread.sleep(100); // ensure the monitor is in wait on the birth
                setLocalNodeStatusInternal(NetworkNode.SCALING_IN);
            }
            return graph;
        } catch(Exception e){
            throw new NetworkDiscoveryException("error starting network discovery agent", e);
        }
    }

    /**
     * Join the workers threads until the are exited or
     * interrupted
     */
    public void join() throws InterruptedException {
        if(broadcastThread != null)
            broadcastThread.join();

        if(networkThread != null)
            networkThread.join();
    }

    /**
     * Stopping the agent will result in all threads closing down gracefully and
     * a final DEATH message being sent to the network if you are operating in
     * broadcast mode.
     */
    public void stop() {
        //-- ensure we send the death certificate, the monitor is waiting within the running loop,
        //-- so death should be the last iteration assuming no interrupts
        currentStatus = NetworkNode.SCALING_OUT;
        synchronized (monitor){
            monitor.notifyAll();
        }
        running = false;
        networkSocket = null;
        networkThread = null;
        broadcastThread = null;
        graph = null;
    }


    /**
     * Change the state of the current node to Healthy. This will immediately cascade
     * out to other nodes listening for network changes allowing your node to be considered
     * active
     */
    public void markLocalNodeHealthy(){
        setLocalNodeStatusInternal(NetworkNode.HEALTHY);
    }

    /**
     * Change the state of the current node to UNhealthy. This will immediately cascade
     * out to other nodes listening for network changes allowing your node to be considered
     * unhealthy and therefore not eligable for work
     */
    public void markLocalNodeUnhealthy(){
        setLocalNodeStatusInternal(NetworkNode.UNHEALTHY);
    }

    /**
     * Return the current tracked instance associated with the agent.
     */
    public NetworkGraph getCurrentNetwork(){
        return graph;
    }

    private final void setLocalNodeStatusInternal(int status){
        currentStatus = status;
        synchronized (monitor){
            monitor.notifyAll();
        }
    }

    protected void startNetworkAgent() throws SocketException{
        if(networkThread == null){
            synchronized (this){
                if(networkThread == null){
                    initSocket();
                    final String threadName = options.getBroadcastListenerThreadName();
                    int readBufferSize = options.getReadBufferSize();
                    networkThread = new Thread(() -> {
                        if(logger.isLoggable(level)){
                            logger.log(level, String.format("creating broadcast listener [%s] bound to socket [%s] with buffer size [%s], running ? [%s]",
                                    threadName, networkSocket.getLocalPort(), readBufferSize, running));
                        }

                        byte[] buff = new byte[readBufferSize];
                        while(running){
                            try {
                                DatagramPacket p = new DatagramPacket(buff, buff.length);
                                networkSocket.receive(p);
                                int length = p.getLength();
                                if(logger.isLoggable(level)){
                                    logger.log(level, String.format("receiving [%s] bytes from [%s]",
                                            length, p.getAddress().getHostAddress()));
                                }
                                receiveFromTransport(NetworkDiscoveryAgentUtils.wrap(buff, length));
                            } catch(Throwable e){
                                logger.log(Level.SEVERE, "encountered an error listening for broadcast traffic;", e);
                            } finally {
                                buff = new byte[readBufferSize];
                            }
                        }
                    }, threadName);
                    networkThread.setDaemon(true);
                    networkThread.setPriority(Thread.MIN_PRIORITY);
                    networkThread.start();
                }
            }
        }
    }

    protected void startBroadcastAgent() {
        if(broadcastThread == null){
            synchronized (this){
                if(broadcastThread == null){
                    final String threadName = options.getBroadcastAgentThreadName();
                    final int writeBufferSize = options.getWriteBufferSize();
                    final int interval = options.getBroadcastIntervalMillis();
                    broadcastThread = new Thread(() -> {
                        if(logger.isLoggable(level)){
                            logger.log(level, String.format("creating broadcast agent [%s] with buffer size [%s], running ? [%s] on interval [%s]",
                                    threadName, writeBufferSize, running, interval));
                        }
                        while(running){
                            try {
                                synchronized (monitor){
                                    monitor.wait(interval);
                                }
                                BroadcastMessage message = generateBroadcastMessage();
                                List<InetAddress> broadcastAddresses = getAllBroadcastAddresses();
                                try (DatagramSocket socket = new DatagramSocket()){
                                    socket.setBroadcast(true);
                                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream(writeBufferSize)) {
                                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                                        oos.writeObject(message);
                                        oos.flush();
                                        byte[] data = baos.toByteArray();
                                        for(InetAddress address : broadcastAddresses) {
                                            if(logger.isLoggable(level)){
                                                logger.log(level, String.format("broadcasting [%s] bytes to network interface [%s] -> [%s]",
                                                        data.length, address, options.getBroadcastPort()));
                                            }
                                            DatagramPacket packet
                                                    = new DatagramPacket(data, data.length, address, options.getBroadcastPort());
                                            socket.send(packet);
                                        }
                                    }
                                } finally {
                                    //-- if this was the birth certificate, we need to flip to ping mode
                                    if(currentStatus == NetworkNode.SCALING_IN){
                                        currentStatus = NetworkNode.HEALTHY;
                                    }
                                }
                            } catch(Throwable e){
                                logger.log(Level.SEVERE, "encountered an error sending broadcast traffic;", e);
                            }
                        }
                    }, threadName);
                    broadcastThread.setDaemon(true);
                    broadcastThread.setPriority(Thread.MIN_PRIORITY);
                    broadcastThread.start();
                }
            }
        }
    }

    protected List<InetAddress> getAllBroadcastAddresses() throws NetworkDiscoveryException {
        try {
            List<InetAddress> l = new ArrayList<>();
            Enumeration<NetworkInterface> interfaces
                    = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() ||
                        !networkInterface.isUp()) {
                    continue;
                }
                networkInterface.getInterfaceAddresses().stream()
                        .map(a -> a.getBroadcast())
                        .filter(Objects::nonNull)
                        .forEach(l::add);
            }
            return l;
        } catch(SocketException e){
            throw new NetworkDiscoveryException(e);
        }
    }

    protected void initSocket() throws SocketException {
        if(networkSocket == null){
            networkSocket = options.getBroadcastPort() > 0 ?
                    new DatagramSocket(options.getBroadcastPort()) : new DatagramSocket();
            networkSocket.setBroadcast(true);
        }
    }

    protected void receiveFromTransport(ByteBuffer buffer)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream in =
                     new ByteArrayInputStream(NetworkDiscoveryAgentUtils.drain(buffer))) {
            ObjectInputStream is = new ObjectInputStream(in);
            receiveMessage((BroadcastMessage) is.readObject());
        }
    }

    protected BroadcastMessage generateBroadcastMessage(){
        BroadcastMessage message = new BroadcastMessage();
        switch(currentStatus){
            case NetworkNode.SCALING_IN: message.setStatus(BroadcastMessage.BIRTH);
                break;
            case NetworkNode.SCALING_OUT: message.setStatus(BroadcastMessage.DEATH);
                break;
            default:
                message.setStatus(BroadcastMessage.PING);
        }
        message.setHost(generateCurrentHostState());
        if(logger.isLoggable(level)){
            logger.log(level, String.format("sending message [%s]", message));
        }
        return message;
    }

    protected NetworkNode generateCurrentHostState(){
        NetworkNode node = new NetworkNode();
        node.setName(nodeName);
        node.setAddress(hostAddress);
        node.setPort(port);
        node.setGroup(groupName);
        node.setEventTime(System.currentTimeMillis());
        node.setStatus(currentStatus);
        return node;
    }

    protected void deriveLocalAddress() throws IOException {
        try (Socket socket = new Socket()){
            if(logger.isLoggable(level)){
                logger.log(level, String.format("deriving network interface.. trying.. [%s]", options.getDynamicLocalEstablishmentUrl()));
            }
            socket.setSoTimeout(1000);
            socket.connect(new InetSocketAddress(options.getDynamicLocalEstablishmentUrl(),
                    options.getDynamicLocalEstablishmentPort()));
            this.hostAddress = socket.getLocalAddress().getHostAddress();
        } catch (IOException e) {
            //-- try a fallback on datagram
            try(final DatagramSocket socket = new DatagramSocket()){
                if(logger.isLoggable(level)){
                    logger.log(level, String.format("deriving attempt 1 failed.. trying datagram on subnet.. [%s]", "8.8.8.8"));
                }
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                this.hostAddress = socket.getLocalAddress().getHostAddress();
            }
        }
    }

    protected void receiveMessage(BroadcastMessage message){
        if(logger.isLoggable(level)){
            logger.log(level, String.format("received message was [%s]", message));
        }
        graph.receiveMessage(message, options.isConsiderPeerProfiles());
    }
}