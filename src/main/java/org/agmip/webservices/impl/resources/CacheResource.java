package org.agmip.webservices.impl.resources;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/cache/")
@Produces(MediaType.APPLICATION_JSON)
public class CacheResource {
    
    @Path("/map")
    @GET
    public String getMapCache() {
        return "{}";
    }
}
