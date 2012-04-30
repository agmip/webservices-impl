package org.agmip.webservices.impl.core;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.raw.pbc.PBClusterConfig;
import com.basho.riak.client.raw.pbc.PBClientConfig;
import com.basho.riak.client.RiakException;

public class RiakPBConnectionFactory {
    private int maxConnections;
    private String[] hosts;


    private RiakPBConnectionFactory(String[] hosts, int maxConnections) {
        this.hosts = hosts;
        this.maxConnections = maxConnections;
    }


    public static RiakPBConnectionFactory newConnection(String[] hosts, int maxConnections) {
        return new RiakPBConnectionFactory(hosts, maxConnections);
    }

    public IRiakClient build() throws RiakException {
        PBClusterConfig pbConfig = new PBClusterConfig(maxConnections);
        for(int i=0; i < hosts.length; i++) {
            if(hosts[i].indexOf(':') == -1) {
                pbConfig.addClient(new PBClientConfig.Builder().withHost(hosts[i]).build());
            } else {
                String[] parts = hosts[i].split(":");
                pbConfig.addClient(new PBClientConfig.Builder().withHost(parts[0]).withPort(Integer.parseInt(parts[1])).build());
            }
        }
        return RiakFactory.newClient(pbConfig);
    }
}
