package com.fred.somusic.services.identify.providers;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.fred.somusic.common.utils.GetWithThrottle;
import com.fred.somusic.common.utils.Log;

// https://developer.spotify.com/web-api/endpoint-reference/
// https://developer.spotify.com/web-api/console/
public class SpotifyProvider extends Provider {

  private final GetWithThrottle fetcher = new GetWithThrottle();

  public SpotifyProvider() {
    super("spotify");
  }

  @Override
  public boolean accept(String query) {
    return query != null
        && (query.contains("spotify:") || query.contains("open.spotify."))
        && !query.contains(".spotify.com/user")
        && !query.contains("spotify:user")
        && !query.contains("spotify.com/app")
        && !query.contains(".spotify.com/playlist")
        && !query.contains(".spotify.com/search")
        && !"http://open.spotify.com/track".equals(query)
        && !"http://open.spotify.com/album".equals(query)
        && !"http://open.spotify.com/artist".equals(query)
        // PENDING(fredL) quick fix, to remove!
        && !query.contains("http://http://");
  }

  @Override
  public Item find(String query) throws IOException {
    // direct lookup when we have a spotify url
    if (accept(query)) {
      return lookup(query);
    } else {
      // otherwise assume a search
      return super.find(query);
    }
  }

  @Override
  public String find(Item lookup) throws IOException {
    String type;
    String query;
    if (lookup instanceof Track) {
      type = "track";
      query = ((Track) lookup).getName() + " " + ((Track) lookup).getArtist();
    } else if (lookup instanceof Artist) {
      type = "artist";
      query = ((Artist) lookup).getName();
    } else if (lookup instanceof Album) {
      type = "album";
      query = ((Album) lookup).getName() + " " + ((Album) lookup).getArtist();
    } else {
      return super.find(lookup);
    }

    // /search/1/track.json?
    String response = fetcher.getBody("https://api.spotify.com/v1/search?type="
        + type + "&q=" + URLEncoder.encode(query, "UTF-8"));
    JSONObject json = (JSONObject) JSONValue.parse(response);
    if (json == null) {
      Log.info("spotify", "JSON is invalid: " + response);
      return null;
    }

    // artists.items[0].external_urls[0]
    JSONObject searches = (JSONObject) json.get(type + "s");
    JSONArray items = (JSONArray) searches.get("items");
    if (items.size() == 0) {
      return null;
    }

    JSONObject item = (JSONObject) items.get(0);
    JSONObject externalUrls = (JSONObject) item.get("external_urls");
    return (String) externalUrls.get("spotify");
  }

  private Item lookup(String spotifyUrl) throws IOException {

    //https://embed.spotify.com/?uri=spotify:track:5VsP1xlRJbb5SYuhvYSu7A
    if (spotifyUrl.startsWith("https://embed.spotify.com/?")) {
      spotifyUrl = spotifyUrl.substring(spotifyUrl.indexOf("?") + 1);
    }

    // remove any query string
    if (spotifyUrl.contains("?")) {
      spotifyUrl = spotifyUrl.substring(0, spotifyUrl.indexOf("?"));
    }

    if (spotifyUrl.contains("track")) {
      return extractTrackV1(spotifyUrl);
    } else if (spotifyUrl.contains("album")) {
      return extractAlbumV1(spotifyUrl);
    } else if (spotifyUrl.contains("artist")) {
      return extractArtistV1(spotifyUrl);
    } else {
      return null;
    }
  }

  private Item extractArtistV1(String spotifyUrl) throws IOException {
    // extract id from url
    String trackId = spotifyUrl.substring(spotifyUrl.indexOf("artist")
        + "artist".length() + 1);

    String response = fetcher.getBody("https://api.spotify.com/v1/artists/"
        + trackId);
    JSONObject json = (JSONObject) JSONValue.parse(response);

    String name = (String) json.get("name");

    Artist artist = new Artist(name);
    artist.setOriginalUrl((String) ((JSONObject) json.get("external_urls"))
        .get("spotify"));
    artist.setImage((String) ((JSONObject) ((JSONArray) json.get("images"))
        .get(0)).get("url"));

    return artist;
  }

  private Item extractAlbumV1(String spotifyUrl) throws IOException {

    // extract id from url
    String trackId = spotifyUrl.substring(spotifyUrl.indexOf("album")
        + "album".length() + 1);

    if (trackId.contains("/")) {
      // it is actually a track within an album
      trackId = trackId.substring(trackId.indexOf("/") + 1);
      return extractTrackV1("https://open.spotify.com/track/" + trackId);
    }

    String response = fetcher.getBody("https://api.spotify.com/v1/albums/"
        + trackId);
    JSONObject json = (JSONObject) JSONValue.parse(response);

    String name = (String) json.get("name");
    String artist = (String) ((JSONObject) ((JSONArray) json.get("artists"))
        .get(0)).get("name");

    Album album = new Album(name, artist);
    album.setOriginalUrl((String) ((JSONObject) json.get("external_urls"))
        .get("spotify"));
    album.setImage((String) ((JSONObject) ((JSONArray) json.get("images"))
        .get(0)).get("url"));

    return album;
  }

  private Item extractTrackV1(String spotifyUrl) throws IOException {
    // extract id from url
    String trackId = spotifyUrl.substring(spotifyUrl.indexOf("track")
        + "track".length() + 1);

    String response = fetcher.getBody("https://api.spotify.com/v1/tracks/"
        + trackId);
    JSONObject json = (JSONObject) JSONValue.parse(response);

    String name = (String) json.get("name");
    String artist = (String) ((JSONObject) ((JSONArray) json.get("artists"))
        .get(0)).get("name");
    String album = (String) ((JSONObject) json.get("album")).get("name");

    Track track = new Track(name, album, artist);
    track.setOriginalUrl((String) ((JSONObject) json.get("external_urls"))
        .get("spotify"));
    track.setImage((String) ((JSONObject) ((JSONArray) ((JSONObject) json
        .get("album")).get("images")).get(0)).get("url"));

    return track;
  }

}
