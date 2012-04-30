package org.agmip.webservices.impl.services.config;

import org.codehaus.jackson.annotate.JsonProperty;

public class DatasetConfig {
    @JsonProperty
    private RiakConfig riak = new RiakConfig();

    public RiakConfig getRiakConfig() {
        return riak;
    }
}
