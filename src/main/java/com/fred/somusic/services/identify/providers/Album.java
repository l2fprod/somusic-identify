package com.fred.somusic.services.identify.providers;

public class Album extends Item {

  private String artist;

  public Album() {
    this("", "");
  }

  public Album(String name, String artist) {
    super("album", name);
    this.artist = artist;
  }

  public String getArtist() {
    return artist;
  }

  @Override
  public String toString() {
    return getName() + " " + artist;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Album) && getName().equals(((Album) obj).getName())
        && getArtist().equals(((Album) obj).getArtist());
  }

}
