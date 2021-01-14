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
