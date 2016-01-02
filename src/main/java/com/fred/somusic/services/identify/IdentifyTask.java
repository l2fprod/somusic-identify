package com.fred.somusic.services.identify;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbInstance;
import org.springframework.cloud.CloudFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IdentifyTask {

  private static Logger logger = Logger.getLogger(IdentifyTask.class);

  @Scheduled(fixedDelay = 5000)
  public void identify() {
    logger.info("Identifying new songs...");

    CouchDbInstance couchDb = new CloudFactory().getCloud().getServiceConnector("cloudant", CouchDbInstance.class,
        null);
    logger.info(couchDb.getAllDatabases());
  }

}
