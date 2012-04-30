package org.agmip.webservices.impl.api;

import com.yammer.dropwizard.logging.Log;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.agmip.core.types.AdvancedHashMap;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

public class Dataset extends CleanDataset {
    private final HashFunction hashFunc = Hashing.sha1();

    @NotEmpty
    @JsonProperty
    private final String id;

    @NotEmpty
    @JsonProperty
    private final String crc;

    // Required for Jersey to not mess up on Serialization
    public Dataset() {
        this.id = "";
        this.crc = "";
        this.data = new AdvancedHashMap();
    }

    public Dataset(AdvancedHashMap<String,String> data) {
        this.data = data;
        this.id = this.cheapCRC();
        this.crc = this.cheapCRC();
    }

    public Dataset(String id, AdvancedHashMap<String,String> data) {
        this.data = data;
        this.id = id;
        this.crc = this.cheapCRC();
    }

    public Dataset(String id, String crc, AdvancedHashMap<String,String> data) {
        this.data = data;
        this.id = id;
        this.crc = crc;
    }

    public String getId() {
        return id;
    }

    public String getCrc() {
        return crc;
    } 

    public String cheapCRC() {
        return hashFunc.newHasher().putString(this.data.toString()).hash().toString();
    }
}
