# Network Discovery Agent
Full, dependency free java implementation of a network discovery agent. Maintain an up-to-date network graph with Node status and information broadcast via UDP.

## Quick start
Configure your details using the code below and run Example. This will yeild a local agent running which when 
marked healthy (on the demo thread) will be discoverable.

```java
public class Example {
    public static void main(String[] args) {
        try {
            //-- create the configuration options for your agent - see documentation for a full list
            //-- of configuration options
            NetworkDiscoveryOptions options = new NetworkDiscoveryOptions().
                    withEncryptionSecret("mySecret");

            //-- instantiate your agent, passing in your local host data that will be optionally
            //-- broadcast if the agent is configured to broadcast as well as receive
            NetworkDiscoveryAgent agent = new NetworkDiscoveryAgent("myTrafficGroup", "myResourceGroup", "myMachine1");

            //-- start the engine, in doing so your network graph will be created. The network graph can from this
            //-- point forward be queried for network status
            NetworkGraph graph = agent.start(options);

            //-- at some point in the future (if you are including broadcasting from this host) ensure you update
            //-- your health state accordingly - not doing so will mean your host remains in the SCALING_IN state
            //-- and therefore is not marked as available
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    agent.markLocalNodeHealthy();
//                    agent.markLocalHostUnhealthy();
                } catch(Exception e){

                }
            }).start();

            //-- you can use the network graph to wait for various clusters to become active
            NetworkNode node =
                    graph.waitOnFirstHealthyNode("myResourceGroup", true, 10000);

            System.out.println("Success, discovered " + node);

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
```