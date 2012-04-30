package org.agmip.webservices.impl.services.config;

import com.yammer.dropwizard.config.Configuration;
import org.codehaus.jackson.annotate.JsonProperty;
import javax.validation.constraints.NotNull;

public class StandaloneConfig extends Configuration {
    @NotNull
    @JsonProperty
    private DatasetConfig dataset = new DatasetConfig();

    public DatasetConfig getDatasetConfig() {
        return dataset;
    }
}
