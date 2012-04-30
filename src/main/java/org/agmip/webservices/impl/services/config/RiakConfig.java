package org.agmip.webservices.impl.services.config;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

public class RiakConfig {
    @NotEmpty
    @JsonProperty
    private String[] hosts;

    @Min(5)
    @Max(65535)
    @JsonProperty
    private int maxConnections = 50;

    @NotEmpty
    @JsonProperty
    private String bucketName;

    public String[] getHosts() {
        return hosts;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getBucketName() {
        return bucketName;
    }
}
