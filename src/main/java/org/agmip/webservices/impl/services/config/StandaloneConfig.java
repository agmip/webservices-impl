package org.agmip.webservices.impl.services.config;

import com.yammer.dropwizard.config.Configuration;
import org.codehaus.jackson.annotate.JsonProperty;
import javax.validation.constraints.NotNull;

public class StandaloneConfig extends Configuration {
    @NotNull
    @JsonProperty
    private DatasetConfig dataset = new DatasetConfig();
    
    @NotNull
    @JsonProperty
    private MetadataConfig metadata = new MetadataConfig();
    
    public DatasetConfig getDatasetConfig() {
        return dataset;
    }
    
    public MetadataConfig getMetadataConfig() {
        return metadata;
    }    
}
