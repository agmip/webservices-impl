package org.agmip.webservices.impl.api;

import java.util.LinkedHashMap;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class CleanDataset {
    @NotEmpty
    @JsonProperty
    protected LinkedHashMap<String,Object> data;

    public CleanDataset() {
        this.data = null;
    }

    public CleanDataset(LinkedHashMap<String,Object> data) {
        this.data = data;
    }

    public LinkedHashMap<String,Object> getData() {
        return data;
    }
}
