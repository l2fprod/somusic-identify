package com.fred.somusic.services.identify.providers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Provider {

  public static Map<String, Provider> providers = new HashMap<String, Provider>();
  static {
    providers.put("spotify", new SpotifyProvider());
  }

  public static String ALL_PROVIDERS = "spotify,deezer,amazon,itunes";
  
  public static Provider getProvider(String location) {
    for (Provider provider : providers.values()) {
      if (provider.accept(location)) {
        return provider;
      }
    }
    return null;
  }

  public enum LookupType {
    track, artist, album
  };

  private final String name;

  protected Provider(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  /**
   * @return true if the provider can handle the query
   */
  public boolean accept(String query) {
    return false;
  }

  /**
   * @return the item matching the query, null if not found
   */
  public Item find(String query) throws IOException {
    return null;
  }

  /**
   * @return the URL for the given object, null if not found
   */
  public String find(Item item) throws IOException {
    return null;
  }

}
