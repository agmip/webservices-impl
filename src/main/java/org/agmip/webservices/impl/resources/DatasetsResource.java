package org.agmip.webservices.impl.resources;

import com.yammer.metrics.annotation.Timed;
import com.yammer.dropwizard.logging.Log;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.bucket.Bucket;

import org.agmip.core.types.AdvancedHashMap;
import org.agmip.webservices.impl.api.CleanDataset;
import org.agmip.webservices.impl.api.Dataset;
//import org.agmip.webservices.impl.api.DatasetInfo;

@Path("/datasets/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DatasetsResource {
    private static final Log LOG = Log.forClass(DatasetsResource.class);
    private static final String hexRegex = "[0-9A-Fa-f]{40}";
    private final HashFunction hashFunc = Hashing.sha1();
    private final IRiakClient riak;
    private final Bucket bucket;

    public DatasetsResource(IRiakClient riak, String bucketName) {
        this.riak = riak;
        try {
            this.bucket = this.riak.fetchBucket(bucketName).execute();
        } catch (RiakException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        }
    }

    @POST
    @Timed
    public Dataset.DatasetInfo createDataset(@Valid CleanDataset pureData) {
        AdvancedHashMap<String,String> dataset = pureData.getData();
        String crc = dataset.remove("system_crc");
        Dataset data = new Dataset(dataset);
        if(crc != null && ! crc.equals(data.getCrc())) {
           throw new WebApplicationException(Response.status(422).entity("Failed CRC check").build()); 
        }
        dataset.put("system_crc", data.getCrc());
        try {
            if(bucket.fetch(data.getId(), AdvancedHashMap.class).execute() != null) {
                throw new WebApplicationException(Response.status(400).entity("This experiment already exists in the database").build());
            }
            bucket.store(data.getId(), dataset).execute();
            // Don't really keep this.
            bucket.delete(data.getId()).execute();
        } catch( RiakException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        }
        return data.getDatasetInfo();
    }

    @GET
    @Timed
    @Path("/{id}")
    public Dataset fetchDataset(@PathParam("id") String id) {
        return new Dataset(id, hashFunc.newHasher().putString(id).hash().toString(), new AdvancedHashMap()); 
    }
}
