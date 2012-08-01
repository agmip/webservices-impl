package org.agmip.webservices.impl.services.config;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;


public class MetadataConfig {
    @JsonProperty
    @NotEmpty
    private String configFile; 

    @JsonProperty
    private RiakConfig riak = new RiakConfig();
    
    public RiakConfig getRiakConfig() {
        return riak;
    }

    public String getConfigFile() {
        return configFile;
    }
}
