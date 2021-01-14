/*
 * Copyright (c) 2021 Simon Johnson <simon622 AT gmail DOT com>
 *
 *  Find me on GitHub:
 *  https://github.com/simon622
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.slj.network.discovery;

import org.slj.network.discovery.model.NetworkNode;
import org.slj.network.discovery.model.NetworkDiscoveryOptions;
import org.slj.network.discovery.model.NetworkGraph;

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
