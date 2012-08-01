package org.agmip.webservices.impl.api;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.yammer.dropwizard.logging.Log;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashFunction;

public class Dataset extends CleanDataset {
    private final HashFunction hashFunc = Hashing.sha1();

    public static class DatasetInfo {
        @JsonProperty
        private final String id;

        @JsonProperty
        private final String crc;

        public DatasetInfo(String id, String crc) {
            this.id = id;
            this.crc = crc;
        }

        public String getId() {
            return id;
        }

        public String getCrc() {
            return crc;
        }
    }

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
        this.data = new LinkedHashMap();
    }

    public Dataset(LinkedHashMap<String,Object> data) {
        this.data = data;
        this.id = this.cheapCRC();
        this.crc = this.cheapCRC();
    }

    public Dataset(String id, LinkedHashMap<String,Object> data) {
        this.data = data;
        this.id = id;
        this.crc = this.cheapCRC();
    }

    public Dataset(String id, String crc, LinkedHashMap<String,Object> data) {
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
        // Ignore the crc and id
        HashMap cleanedData = new HashMap(this.data);
        cleanedData.remove("system_crc");
        cleanedData.remove("id");
        return hashFunc.newHasher().putString(cleanedData.toString()).hash().toString();
    }

    public DatasetInfo getDatasetInfo() {
        return new Dataset.DatasetInfo(id, crc);
    }
}
