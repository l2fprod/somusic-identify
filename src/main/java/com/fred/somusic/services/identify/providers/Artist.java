package com.fred.somusic.services.identify.providers;


public class Artist extends Item {

  public Artist() {
    this("");
  }
  
  public Artist(String name) {
    super("artist", name);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Artist)
        && getName().equals(((Artist) obj).getName());
  }

}
