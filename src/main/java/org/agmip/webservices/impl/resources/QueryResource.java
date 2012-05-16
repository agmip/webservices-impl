package org.agmip.webservices.impl.resources;

import java.util.Iterator;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.query.MapReduceResult;
import com.basho.riak.client.query.functions.NamedJSFunction;
import com.basho.riak.client.query.indexes.BinIndex;
import com.basho.riak.client.raw.query.indexes.BinValueQuery;
import com.basho.riak.client.raw.query.indexes.IndexQuery;
import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.annotation.Timed;
import org.agmip.core.types.AdvancedHashMap;
import org.agmip.webservices.impl.core.MetadataFilter;

/**
 * The QueryResource is used to query the metadata server for an actual
 * experiment.
 * 
 * @todo Lookup and utilize basic weights system.
 */

@Path("/query/")
public class QueryResource {
    private final static Log LOG = Log.forClass(QueryResource.class);
    private final IRiakClient riak;
    private final Bucket metabucket;
    private final String metabucketName;

    public QueryResource(IRiakClient client, String metabucketName) {
        this.riak = client;
        this.metabucketName = metabucketName;
        try {
            this.metabucket = this.riak.fetchBucket(this.metabucketName).execute();
        } catch (RiakException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        }
    }


    /**
     * Primary searching function
     *
     * @todo Allow for ranged searches (ex. lat, lon)
     * @todo Implement MR filtering on all other fields
     */
    @GET
    @Timed
    public String searchMe(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> searchParams = uriInfo.getQueryParameters();
        String baseSearchParam = findBestParam(searchParams.keySet());
        MapReduceResult results;
        if(baseSearchParam.equals("")) {
            throw new WebApplicationException(Response
                .status(400)
                .entity("Searching without indexes not implemented")
                .build());
        }
        try {
            IndexQuery iq = new BinValueQuery(BinIndex.named(baseSearchParam), metabucketName, searchParams.getFirst(baseSearchParam));
            results = riak.mapReduce(iq)
                          .addMapPhase(new NamedJSFunction("Riak.mapValuesJson"), true)
                          .execute();
        } catch (RiakException e) {
            throw new WebApplicationException(Response
                .status(400)
                .entity(e.getMessage())
                .build());
        }
        return results.getResultRaw();
    }
    
    /**
     * Find the best parameter (lowest weight) to search on for 2i.
     * If there are more than one with the same lowest weight, just
     * pick an arbitary one.
     *
     * @todo Add some protection (params ! null)
     * @param params A set of parameters to weigh.
     * @return the parameter with the lowest weight.
     */
    private String findBestParam(Set params) {
        int winWeight = 11; // Our amps don't go to 11 :(
        String winParam = "";
        AdvancedHashMap weights = MetadataFilter.INSTANCE.getWeights();
        Set fields = MetadataFilter.INSTANCE.getIndexedMetadata();
        Iterator i = params.iterator();
        // This SHOULD get anything that is indexed in the search,
        // to prevent from MapReducing the whole bucket.
        while(i.hasNext()) {
            String var = i.next().toString();
            if(fields.contains(var)) {
                int weight = Integer.parseInt(weights.getOr(var, "10").toString());
                if( weight < winWeight ) {
                    winWeight = weight;
                    winParam = var;
                }
            }
        }
        LOG.debug("Winning weight: "+winWeight);
        LOG.debug("Winning param: "+winParam);
        return winParam;
    }
}
