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

package org.slj.network.discovery.model;

public class NetworkDiscoveryOptions {

    /**
     * By default the port the agent will listen for events is 2552
     */
    public static int DEFAULT_BROADCAST_PORT = 2552;

    /**
     * The default broadcast interval is 10000 milliseconds
     */
    public static int DEFAULT_BROADCAST_INTERVAL_MILLIS = 10000;

    /**
     * The default datagram buffer size is 2048
     */
    public static int DEFAULT_READ_BUFFER_SIZE = 2048;

    /**
     * The default write buffer for the object stream is 256
     */
    public static int DEFAULT_WRITE_BUFFER_SIZE = 256;

    /**
     * By default broadcast will include peer details
     */
    public static boolean DEFAULT_BROADCAST_PEER_PROFILES = true;

    /**
     * By default peer profiles will be considered
     */
    public static boolean DEFAULT_CONSIDER_PEER_PROFILES = true;

    /**
     * By default the name of the listener thread is network-discovery-agent-listener
     */
    public static String DEFAULT_LISTENER_THREAD_NAME = "network-discovery-agent-listener";

    /**
     * By default the name of the agent thread is network-discovery-agent
     */
    public static String DEFAULT_AGENT_THREAD_NAME = "network-discovery-agent";

    /**
     * Network will be enabled by default
     */
    public static boolean DEFAULT_NETWORK_ENABLED = true;

    /**
     * Broadcast will be enabled by default
     */
    public static boolean DEFAULT_BROADCAST_ENABLED = true;

    /**
     * By default google.com will be used to determine local address
     */
    public static String DEFAULT_DYNAMIC_LOCAL_ESTABLISHMENT_URL = "google.com";

    /**
     * By default port 80 will be used
     */
    public static int DEFAULT_DYNAMIC_LOCAL_ESTABLISHMENT_PORT = 80;

    /**
     * By default the local host will be returned in graph queries
     */
    public static boolean DEFAULT_VERBOSE_LOGGING_ENABLED = true;

    /**
     * By default encryption of message bodies is enabled
     */
    public static boolean DEFAULT_ENCRYPTION_ENABLED = true;

    /**
     * There is NO encryption secret set by default this must before use if using encryption
     */
    public static final String DEFAULT_ENCRYPTION_SECRET = null;

    /**
     * By default a node will NOT be marked healthy after the birth is broadcast, it needs to be done
     * by the application
     */
    public static final boolean DEFAULT_NODE_MARKED_HEALTHY_AFTER_BIRTH = false;

    boolean networkEnabled = DEFAULT_NETWORK_ENABLED;
    boolean broadcastEnabled = DEFAULT_BROADCAST_ENABLED;
    boolean broadcastPeerProfiles = DEFAULT_BROADCAST_PEER_PROFILES;
    boolean considerPeerProfiles = DEFAULT_CONSIDER_PEER_PROFILES;
    int broadcastIntervalMillis = DEFAULT_BROADCAST_INTERVAL_MILLIS;
    int readBufferSize = DEFAULT_READ_BUFFER_SIZE;
    int writeBufferSize = DEFAULT_WRITE_BUFFER_SIZE;
    String broadcastListenerThreadName = DEFAULT_LISTENER_THREAD_NAME;
    String broadcastAgentThreadName = DEFAULT_AGENT_THREAD_NAME;
    int broadcastPort = DEFAULT_BROADCAST_PORT;
    String dynamicLocalEstablishmentUrl = DEFAULT_DYNAMIC_LOCAL_ESTABLISHMENT_URL;
    int dynamicLocalEstablishmentPort = DEFAULT_DYNAMIC_LOCAL_ESTABLISHMENT_PORT;
    boolean verboseLoggingEnabled = DEFAULT_VERBOSE_LOGGING_ENABLED;
    boolean encryptedEnabled = DEFAULT_ENCRYPTION_ENABLED;
    String encryptionSecret = DEFAULT_ENCRYPTION_SECRET;
    boolean nodeMarkedHealthyAfterBirth = DEFAULT_NODE_MARKED_HEALTHY_AFTER_BIRTH;

    public NetworkDiscoveryOptions withNodeMarkedHealthyAfgterBirth(boolean nodeMarkedHealthyAfterBirth){
        this.nodeMarkedHealthyAfterBirth = nodeMarkedHealthyAfterBirth;
        return this;
    }

    public NetworkDiscoveryOptions withEncryptionEnabled(boolean encryptionEnabled){
        this.encryptedEnabled = encryptionEnabled;
        return this;
    }

    public NetworkDiscoveryOptions withEncryptionSecret(String encryptionSecret){
        this.encryptionSecret = encryptionSecret;
        return this;
    }

    public NetworkDiscoveryOptions withVerboseLoggingEnabled(boolean verboseLoggingEnabled){
        this.verboseLoggingEnabled = verboseLoggingEnabled;
        return this;
    }

    public NetworkDiscoveryOptions withBroadcastPort(int broadcastPort){
        this.broadcastPort = broadcastPort;
        return this;
    }

    public NetworkDiscoveryOptions withBroadcastIntervalMillis(int broadcastIntervalMillis){
        this.broadcastIntervalMillis = broadcastIntervalMillis;
        return this;
    }

    public NetworkDiscoveryOptions withReadBufferSize(int readBufferSize){
        this.readBufferSize = readBufferSize;
        return this;
    }

    public NetworkDiscoveryOptions withWriteBufferSize(int writeBufferSize){
        this.writeBufferSize = writeBufferSize;
        return this;
    }

    public NetworkDiscoveryOptions withBroadcastPeerProfiles(boolean broadcastPeerProfiles){
        this.broadcastPeerProfiles = broadcastPeerProfiles;
        return this;
    }

    public NetworkDiscoveryOptions withConsiderPeerProfiles(boolean considerPeerProfiles){
        this.considerPeerProfiles = considerPeerProfiles;
        return this;
    }

    public NetworkDiscoveryOptions withNetworkEnabled(boolean networkEnabled){
        this.networkEnabled = networkEnabled;
        return this;
    }

    public NetworkDiscoveryOptions withBroadcastEnabled(boolean broadcastEnabled){
        this.broadcastEnabled = broadcastEnabled;
        return this;
    }

    public NetworkDiscoveryOptions withBroadcastListenerThreadName(String broadcastListenerThreadName){
        this.broadcastListenerThreadName = broadcastListenerThreadName;
        return this;
    }

    public NetworkDiscoveryOptions withBroadcastAgentThreadName(String broadcastAgentThreadName){
        this.broadcastAgentThreadName = broadcastAgentThreadName;
        return this;
    }

    public NetworkDiscoveryOptions withDynamicLocalEstablishmentUrl(String dynamicLocalEstablishmentUrl){
        this.dynamicLocalEstablishmentUrl = dynamicLocalEstablishmentUrl;
        return this;
    }

    public NetworkDiscoveryOptions withDynamicLocalEstablishmentPort(int dynamicLocalEstablishmentPort){
        this.dynamicLocalEstablishmentPort = dynamicLocalEstablishmentPort;
        return this;
    }

    public String getBroadcastListenerThreadName() {
        return broadcastListenerThreadName;
    }

    public String getBroadcastAgentThreadName() {
        return broadcastAgentThreadName;
    }

    public boolean isBroadcastPeerProfiles() {
        return broadcastPeerProfiles;
    }

    public boolean isConsiderPeerProfiles() {
        return considerPeerProfiles;
    }

    public int getBroadcastIntervalMillis() {
        return broadcastIntervalMillis;
    }

    public int getBroadcastPort() {
        return broadcastPort;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public String getDynamicLocalEstablishmentUrl() {
        return dynamicLocalEstablishmentUrl;
    }

    public int getDynamicLocalEstablishmentPort() {
        return dynamicLocalEstablishmentPort;
    }

    public boolean isVerboseLoggingEnabled() {
        return verboseLoggingEnabled;
    }

    public boolean isNetworkEnabled() {
        return networkEnabled;
    }

    public boolean isBroadcastEnabled() {
        return broadcastEnabled;
    }

    public boolean isEncryptedEnabled() {
        return encryptedEnabled;
    }

    public String getEncryptionSecret() {
        return encryptionSecret;
    }

    public boolean isNodeMarkedHealthyAfterBirth() {
        return nodeMarkedHealthyAfterBirth;
    }
}
