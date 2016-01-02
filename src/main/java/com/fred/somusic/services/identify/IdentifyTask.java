package com.fred.somusic.services.identify;

import java.util.List;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.springframework.cloud.CloudFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fred.somusic.common.BaseTask;
import com.fred.somusic.common.model.Song;
import com.fred.somusic.common.model.Song.Status;
import com.fred.somusic.common.utils.Log;
import com.fred.somusic.services.identify.providers.Album;
import com.fred.somusic.services.identify.providers.Artist;
import com.fred.somusic.services.identify.providers.Item;
import com.fred.somusic.services.identify.providers.Provider;
import com.fred.somusic.services.identify.providers.Track;

@Component
public class IdentifyTask extends BaseTask {

  private static Logger logger = Logger.getLogger(IdentifyTask.class);

  @Scheduled(fixedDelay = 5000)
  public void identify() {
    logger.info("Identifying new songs...");

    CouchDbInstance couchDb = new CloudFactory().getCloud().getServiceConnector("cloudant", CouchDbInstance.class,
        null);
    logger.info(couchDb.getAllDatabases());

    CouchDbConnector db = getSongDb();
    ViewQuery findUnprocessed = new ViewQuery().designDocId("_design/songs").viewName("by_state").key(Status.NEW)
        .includeDocs(true).limit(1000);
    List<Song> songs = db.queryView(findUnprocessed, Song.class);
    Log.info("identify", "Found " + songs.size() + " songs to process");
    int index = 0;
    int count = songs.size();
    for (Song song : songs) {
      index++;
      Log.info("identify", "Processing [" + index + "/" + count + "] " + song.getLink());

      Provider provider = Provider.getProvider(song.getLink());
      if (provider == null) {
        song.setState(Status.FAILED);
        db.update(song);
        continue;
      }

      Item resolved = null;
      try {
        resolved = provider.find(song.getLink());
      } catch (Exception e) {
        Log.info("identify", e.getLocalizedMessage());
        song.setState(Status.FAILED);
      }

      if (resolved == null) {
        // accepted but not resolved, forget it
        song.setState(Status.FAILED);
      } else if (resolved instanceof Artist) {
        song.setArtist(((Artist) resolved).getName());
        song.setState(Status.DATA_FOUND);
      } else if (resolved instanceof Album) {
        song.setArtist(((Album) resolved).getArtist());
        song.setAlbum(((Album) resolved).getName());
        song.setState(Status.DATA_FOUND);
      } else if (resolved instanceof Track) {
        song.setArtist(((Track) resolved).getArtist());
        song.setTitle(((Track) resolved).getName());
        song.setAlbum(((Track) resolved).getAlbum());
        song.setState(Status.DATA_FOUND);
      }

      if (resolved != null && resolved.getImage() != null) {
        song.setImage(resolved.getImage());
        song.setState(Status.IMAGE_FOUND);
      }

      Log.info("identify", song);
      db.update(song);
    }
    Log.info("identify", "Processing complete");
  }
}
