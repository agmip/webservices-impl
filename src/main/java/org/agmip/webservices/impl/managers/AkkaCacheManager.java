package org.agmip.webservices.impl.managers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.dropwizard.logging.Log;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.bucket.Bucket;

import java.util.ArrayList;

import org.agmip.core.types.AdvancedHashMap;

public class AkkaCacheManager implements Managed {
    private ActorSystem cacheSystem;
    private ActorRef    mapCache;
    private ActorRef    countryCache;
    private ActorRef    cropCache;
    
    private final IRiakClient riak;

    public final static Log LOG = Log.forClass(AkkaCacheManager.class);

    public AkkaCacheManager(IRiakClient client) {
        this.riak = client;
    }

    public void start() {
        LOG.debug("Starting akka for Caches");
        cacheSystem = ActorSystem.create("CacheSystem");
    }

    public void stop() {
        LOG.debug("Stopping akka for Caches");
        cacheSystem.shutdown();
    }

    public ActorSystem getCacheSystem() {
        return cacheSystem;
    }

    public ActorRef getMapCache() {
        return mapCache;
    }

    public ActorRef getCountryCache() {
        return countryCache;
    }

    public ActorRef getCropCache() {
        return cropCache;
    }

    // Messages
    static class Map {
        private final AdvancedHashMap mapInfo;

        public Map(AdvancedHashMap mapInfo) {
            this.mapInfo = mapInfo;
        }

        public AdvancedHashMap getMapInfo() {
            return mapInfo;
        }
    }
    static class Country{
        private final String country;

        public Country(String country) {
            this.country = country.toUpperCase();
        }

        public String getCountry() {
            return country;
        }
    }

    static class Crop {
        private final String crop;

        public Crop(String crop) {
            this.crop = crop.toUpperCase();
        }

        public String getCrop() {
            return crop;
        }
    }


    // Actors
    static class CountryCacheWorker extends UntypedActor {
        private final Bucket bucket;
 
        public CountryCacheWorker(IRiakClient riak) throws RiakException {
            this.bucket = riak.fetchBucket("cache").execute();
        }

        public void onReceive(Object message) throws RiakException {
            if( message instanceof Country ) {
                Country c = (Country) message;
                ArrayList<String> cache = bucket.fetch("country", ArrayList.class).execute();
                if( ! cache.contains(c) ) {
                    LOG.debug("Added new Country to the Country cache");
                    cache.add(c.getCountry());
                    bucket.store(cache).execute();
                }
            } else {
                unhandled(message);
            }
        }
    }
    
    static class CropCacheWorker extends UntypedActor {
        private final Bucket bucket;

        public CropCacheWorker(IRiakClient riak) throws RiakException {
            this.bucket = riak.fetchBucket("cache").execute();
        }

        public void onReceive(Object message) throws RiakException {
            if( message instanceof Crop ) {
                Crop c = (Crop) message;
                ArrayList<String> cache = bucket.fetch("crop", ArrayList.class).execute();
                if( ! cache.contains(c) ) {
                    LOG.debug("Added new Crop to the Crop cache");
                    cache.add(c.getCrop());
                    bucket.store(cache).execute();
                }
            } else {
                unhandled(message);
            }
        }
    }

    static class MapInfoCacheWorker extends UntypedActor {
        private final Bucket bucket;

        public MapInfoCacheWorker(IRiakClient riak) throws RiakException {
            this.bucket = riak.fetchBucket("cache").execute();
        }

        public void onReceive(Object message) throws RiakException {
            if( message instanceof Map ) {
                Map m = (Map) message;
                ArrayList<AdvancedHashMap<String, String>> cache = bucket.fetch("map", ArrayList.class).execute();
                if( ! cache.contains(m) ) {
                    LOG.debug("Added new MapPoint to Map Cache");
                    cache.add(m.getMapInfo());
                    bucket.store(cache).execute();
                }
            } else {
                unhandled(message);
            }
        }
    }
}
