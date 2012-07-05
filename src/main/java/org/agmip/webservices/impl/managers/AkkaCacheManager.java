package org.agmip.webservices.impl.managers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.dropwizard.logging.Log;

import org.agmip.core.types.AdvancedHashMap;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.bucket.Bucket;

public class AkkaCacheManager implements Managed {
    ActorSystem cacheSystem;
    ActorRef    mapCache;
    ActorRef    countryCache;
    ActorRef    cropCache;
    
    IRiakClient riak;

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
    static class CropCacheWorker extends UntypedActor {
        public void doCache() {

        }
        public void onReceive(Object message) {
            if( message instanceof Crop ) {
            }
        }
    }
}
