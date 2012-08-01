package org.agmip.webservices.impl.core;

import java.util.LinkedHashMap;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public enum MetadataFilter {
    INSTANCE;

    private final CopyOnWriteArraySet<String> metadata = new CopyOnWriteArraySet<String>();
    private final CopyOnWriteArraySet<String> required = new CopyOnWriteArraySet<String>();
    private final CopyOnWriteArraySet<String> indexed  = new CopyOnWriteArraySet<String>();
    private final LinkedHashMap<String, Integer> weights = new LinkedHashMap();

    MetadataFilter() {}

    public CopyOnWriteArraySet getMetadata() {
        return metadata;
    }

    public CopyOnWriteArraySet getRequiredMetadata() {
        return required;
    }

    public CopyOnWriteArraySet getIndexedMetadata() {
        return indexed;
    }
    
    public LinkedHashMap getWeights() {
        return weights;
    }

    public void addMetadata(String item) {
        metadata.add(item);
    }

    public void addIndexedMetadata(String item) {
        metadata.add(item);
        indexed.add(item);
    }

    public void addRequiredMetadata(String item) {
        metadata.add(item);
        required.add(item);
    }

    public void addRequiredIndexedMetadata(String item) {
        metadata.add(item);
        required.add(item);
        indexed.add(item);
    }
    
    public void addWeight(String item, int value) {
        weights.put(item, value);
    }

    public void removeMetadata(String item) {
        metadata.remove(item);
        required.remove(item);
        indexed.remove(item);
    }

    public void removeIndexedMetadata(String item) {
        indexed.remove(item);
    }

    public void removeRequiredMetadata(String item) {
        required.remove(item);
    }
}
