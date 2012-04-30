package org.agmip.webservices.impl.api;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.agmip.core.types.AdvancedHashMap;

public class CleanDataset {
    @NotEmpty
    @JsonProperty
    protected AdvancedHashMap<String,String> data;

    public CleanDataset() {
        this.data = null;
    }

    public CleanDataset(AdvancedHashMap<String,String> data) {
        this.data = data;
    }

    public AdvancedHashMap<String,String> getData() {
        return data;
    }
}
