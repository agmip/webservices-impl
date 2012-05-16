package org.agmip.webservices.impl.managers;

import java.io.FileReader;
import java.io.IOException;
import au.com.bytecode.opencsv.CSVReader;
import org.agmip.webservices.impl.core.MetadataFilter;

import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.dropwizard.logging.Log;

public class MetadataManager implements Managed {
    private final static Log LOG = Log.forClass(MetadataManager.class);
    private final String fileName;

    public MetadataManager(String file) {
        this.fileName = file;
    }

    public void start() throws IOException {
        LOG.debug("Reading Metadata file...");
        CSVReader reader = new CSVReader(new FileReader(fileName));
        String[] nextLine;
        while((nextLine = reader.readNext()) != null) {
            String var = nextLine[0].toLowerCase();
            if(! nextLine[1].equals("")) {
                if(! nextLine[2].equals("")) {
                    MetadataFilter.INSTANCE.addRequiredIndexedMetadata(var);
                } else {
                    MetadataFilter.INSTANCE.addIndexedMetadata(var);
                }
            } else if(! nextLine[2].equals("")) {
                MetadataFilter.INSTANCE.addRequiredMetadata(var);
            } else {
                MetadataFilter.INSTANCE.addMetadata(var);
            }
            if(! nextLine[3].equals("")) {
                MetadataFilter.INSTANCE.addWeight(var, Integer.parseInt(nextLine[3]));
            }
        }
        LOG.info("Indexed Metadata: "+MetadataFilter.INSTANCE.getIndexedMetadata());
        LOG.info("Required Metadata: "+MetadataFilter.INSTANCE.getRequiredMetadata());
        LOG.info("Weights:"+MetadataFilter.INSTANCE.getWeights());
    }

    public void stop() {
    }
}
