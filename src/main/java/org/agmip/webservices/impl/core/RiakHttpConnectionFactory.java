package org.agmip.webservices.impl.core;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.raw.http.HTTPClusterConfig;
import com.basho.riak.client.raw.http.HTTPClientConfig;
import com.basho.riak.client.RiakException;

public class RiakHttpConnectionFactory {
    private int maxConnections;
    private String[] hosts;


    private RiakHttpConnectionFactory(String[] hosts, int maxConnections) {
        this.hosts = hosts;
        this.maxConnections = maxConnections;
    }


    public static RiakHttpConnectionFactory newConnection(String[] hosts, int maxConnections) {
        return new RiakHttpConnectionFactory(hosts, maxConnections);
    }

    public IRiakClient build() throws RiakException {
        HTTPClusterConfig httpConfig = new HTTPClusterConfig(maxConnections);
        for(int i=0; i < hosts.length; i++) {
            if(hosts[i].indexOf(':') == -1) {
                httpConfig.addClient(new HTTPClientConfig.Builder().withHost(hosts[i]).build());
            } else {
                String[] parts = hosts[i].split(":");
                httpConfig.addClient(new HTTPClientConfig.Builder().withHost(parts[0]).withPort(Integer.parseInt(parts[1])).build());
            }
        }
        return RiakFactory.newClient(httpConfig);
    }
}
