package com.fred.somusic.services.identify.providers;

public abstract class Item {

  private Long id;

  private String type;
  private String name;
  private String image;

  private transient String originalUrl;

  public Item() {
  }

  protected Item(String type, String name) {
    this.type = type;
    this.name = name;
  }

  public Long getId() {
    return id;
  }
  
  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setOriginalUrl(String originalUrl) {
    this.originalUrl = originalUrl;
  }

  public String getOriginalUrl() {
    return originalUrl;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public abstract boolean equals(Object obj);

}
