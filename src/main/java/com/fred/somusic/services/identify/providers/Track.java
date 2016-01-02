package com.fred.somusic.services.identify.providers;


public class Track extends Item {

  private String artist;
  private String album;

  public Track() {
    this("");
  }
  
  public Track(String name) {
    this(name, "", "");
  }

  public Track(String name, String album, String artist) {
    super("track", name);
    this.album = album;
    this.artist = artist;
  }

  public String getArtist() {
    return artist;
  }

  public String getAlbum() {
    return album;
  }

  @Override
  public String toString() {
    return getName() + " " + album + " " + artist;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Track) && getName().equals(((Track) obj).getName())
        && getAlbum().equals(((Track) obj).getAlbum())
        && getArtist().equals(((Track) obj).getArtist());
  }

}
