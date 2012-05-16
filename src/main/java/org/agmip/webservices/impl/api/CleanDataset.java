package org.agmip.webservices.impl.api;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.agmip.core.types.AdvancedHashMap;

public class CleanDataset {
    @NotEmpty
    @JsonProperty
    protected AdvancedHashMap<String,Object> data;

    public CleanDataset() {
        this.data = null;
    }

    public CleanDataset(AdvancedHashMap<String,Object> data) {
        this.data = data;
    }

    public AdvancedHashMap<String,Object> getData() {
        return data;
    }
}
