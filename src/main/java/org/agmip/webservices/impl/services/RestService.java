package org.agmip.webservices.impl.services;

import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.logging.Log;
import com.yammer.dropwizard.Service;

import org.agmip.webservices.impl.core.RiakPBConnectionFactory;
import org.agmip.webservices.impl.core.RiakHttpConnectionFactory;
import org.agmip.webservices.impl.managers.MetadataManager;
import org.agmip.webservices.impl.managers.AkkaCacheManager;
import org.agmip.webservices.impl.resources.DatasetsResource;
import org.agmip.webservices.impl.resources.QueryResource;
import org.agmip.webservices.impl.services.config.StandaloneConfig;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;

public class RestService extends Service<StandaloneConfig> {
    public static final Log LOG = Log.forClass(RestService.class);
    public static void main(String[] args) throws Exception {
        new RestService().run(args);
    }

    private RestService() {
        super("rest-service");
    }

    @Override
    protected void initialize(StandaloneConfig config, Environment env) throws RiakException {
        // Configuration
        final String[] dsRiakHosts          = config.getDatasetConfig().getRiakConfig().getHosts();
        final int      dsRiakMaxConn        = config.getDatasetConfig().getRiakConfig().getMaxConnections();
        final String   dsRiakBucket         = config.getDatasetConfig().getRiakConfig().getBucketName();
        final String   dsRiakConnectionType = config.getDatasetConfig().getRiakConfig().getConnectionType();
        final String[] mdRiakHosts          = config.getMetadataConfig().getRiakConfig().getHosts();
        final int      mdRiakMaxConn        = config.getMetadataConfig().getRiakConfig().getMaxConnections();
        final String   mdRiakBucket         = config.getMetadataConfig().getRiakConfig().getBucketName();
        final String   mdRiakConnectionType = config.getMetadataConfig().getRiakConfig().getConnectionType();
        final IRiakClient dsRiak;
        final IRiakClient mdRiak;

        // Riak clients
        if(dsRiakConnectionType.equals("pb")) {
            LOG.debug("Using protobuffers");
            dsRiak = RiakPBConnectionFactory.newConnection(dsRiakHosts, dsRiakMaxConn).build();
        } else {
            LOG.debug("Using Http");
            dsRiak = RiakHttpConnectionFactory.newConnection(dsRiakHosts, dsRiakMaxConn).build();
        }
        if(mdRiakConnectionType.equals("pb")) {
             LOG.debug("Using protobuffers");
             mdRiak = RiakPBConnectionFactory.newConnection(mdRiakHosts, mdRiakMaxConn).build();
         } else {
             LOG.debug("Using Http");
             mdRiak = RiakHttpConnectionFactory.newConnection(mdRiakHosts, mdRiakMaxConn).build();
         }
 
        env.manage(new MetadataManager("metadata.csv"));
        env.manage(new AkkaCacheManager());
        env.addResource(new DatasetsResource(dsRiak, dsRiakBucket, mdRiak, mdRiakBucket));
        env.addResource(new QueryResource(mdRiak, mdRiakBucket));
    }
}
