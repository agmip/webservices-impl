package org.agmip.webservices.impl.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.builders.RiakObjectBuilder;
import com.basho.riak.client.http.util.Constants;

import org.codehaus.jackson.map.ObjectMapper;

import org.agmip.core.types.AdvancedHashMap;
import org.agmip.webservices.impl.api.CleanDataset;
import org.agmip.webservices.impl.api.Dataset;
import org.agmip.webservices.impl.core.MetadataFilter;

@Path("/datasets/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DatasetsResource {
    private static final Log LOG = Log.forClass(DatasetsResource.class);
    private static final String hexRegex = "[0-9A-Fa-f]{40}";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HashFunction hashFunc = Hashing.sha1();
    private final IRiakClient riak;
    private final IRiakClient mdriak;
    private final Bucket bucket;
    private final Bucket metabucket;
    private final String mdBucketName;

    public DatasetsResource(IRiakClient riak, String bucketName, IRiakClient mdriak, String mdBucketName) {
        this.riak = riak;
        this.mdriak = mdriak;
        this.mdBucketName = mdBucketName;
        try {
            this.bucket = this.riak.fetchBucket(bucketName).execute();
            this.metabucket = this.mdriak.fetchBucket(mdBucketName).execute();
        } catch (RiakException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        }
    }

    @POST
    @Timed
    public Dataset.DatasetInfo createDataset(@Valid CleanDataset pureData) {
        // Extract the required metadata fields from the data first.
        AdvancedHashMap<String,Object> dataset = pureData.getData();
        AdvancedHashMap<String,Object> dsMetadata = dataset.extract(MetadataFilter.INSTANCE.getRequiredMetadata());
        if(dsMetadata.size() == 0) {
            throw new WebApplicationException(Response.status(422).entity("Missing required metadata").build());
        }
        // Extract the rest of the metadata, now that the required is true.
        dsMetadata.put(dataset.extract(MetadataFilter.INSTANCE.getMetadata()));
        String crc = (String) dataset.get("system_crc");
        Dataset data = new Dataset(dataset);
        if(crc != null && ! crc.equals(data.getCrc())) {
           throw new WebApplicationException(Response.status(422).entity("Failed CRC check").build()); 
        }
        try {
            if(bucket.fetch(data.getId(), AdvancedHashMap.class).execute() != null) {
                throw new WebApplicationException(Response.status(400).entity("This experiment already exists in the database").build());
            }
            // Overwrite any ID given by the user. We should generate it.
            dataset.put("id", data.getId());
            dataset.put("system_crc", data.getCrc());
            bucket.store(data.getId(), dataset).execute();
        } catch( RiakException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        }
        try {
            // This should be a IRiakObject, to support 2i
            IRiakObject obj = RiakObjectBuilder.newBuilder(mdBucketName, data.getId())
                .withContentType(Constants.CTYPE_JSON_UTF8)
                .withValue("{}")
                .build();

            //Serialize the data into JSON
            obj.setValue(mapper.writeValueAsBytes(dsMetadata));
            for(Map.Entry<String,Object> e : dsMetadata.entrySet()) {
                obj.addIndex(e.getKey(), e.getValue().toString());
            }
            metabucket.store(obj).execute();
        } catch(RiakException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        } catch(IOException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        }
        // At this point, put information into the caches. Start with a map cache.
        return data.getDatasetInfo();
    }

    @GET
    @Timed
    @Path("/{id}")
    public Dataset fetchDataset(@PathParam("id") String id) {
        Dataset dataset;
        try {
           AdvancedHashMap<String,Object> data = bucket.fetch(id, AdvancedHashMap.class).execute();
           if(data == null) {
               throw new WebApplicationException(Response.status(404).entity("This id does not exist").build());
           }
           dataset = new Dataset(id, (String) data.get("system_crc"), data);
        } catch (RiakException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        }
        return dataset;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteDataset(@PathParam("id") String id) {
        try {
            bucket.delete(id).execute();
            metabucket.delete(id).execute();
        } catch (RiakException e) {
            throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
        }
        return Response.status(204).build();
    }
}
