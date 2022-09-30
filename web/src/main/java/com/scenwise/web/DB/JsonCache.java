package com.scenwise.web.DB;

import java.time.Instant;

public class JsonCache {
  private Instant lastCacheTime;
  private String jsonString;
  public JsonCache(String jsonString) {
    this.lastCacheTime = Instant.now();
    this.jsonString = jsonString;
  }

  public boolean isCacheStale() {
    return (Instant.now().getEpochSecond() - lastCacheTime.getEpochSecond() > 600);
  }

  public String getCache(){
    if (!isCacheStale()) {
      return jsonString;
    }
    return "";
  }
}
