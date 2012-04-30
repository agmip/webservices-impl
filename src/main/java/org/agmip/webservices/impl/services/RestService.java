package org.agmip.webservices.impl.services;

import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.Service;

import org.agmip.webservices.impl.core.RiakPBConnectionFactory;
import org.agmip.webservices.impl.resources.DatasetsResource;
import org.agmip.webservices.impl.services.config.StandaloneConfig;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;

public class RestService extends Service<StandaloneConfig> {
    public static void main(String[] args) throws Exception {
        new RestService().run(args);
    }

    private RestService() {
        super("rest-service");
    }

    @Override
    protected void initialize(StandaloneConfig config, Environment env) throws RiakException {
        // Configuration
        final String[] dsRiakHosts   = config.getDatasetConfig().getRiakConfig().getHosts();
        final int      dsRiakMaxConn = config.getDatasetConfig().getRiakConfig().getMaxConnections();
        final String   dsRiakBucket  = config.getDatasetConfig().getRiakConfig().getBucketName();
       
        // Riak clients
        final IRiakClient dsRiak = RiakPBConnectionFactory.newConnection(dsRiakHosts, dsRiakMaxConn).build();
        
        env.addResource(new DatasetsResource(dsRiak, dsRiakBucket));
    }
}
