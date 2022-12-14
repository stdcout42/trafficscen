package com.scenwise.web.DB;

public enum Table {

  /**
   *  Table names (in postgres db)
   */
  TRAFFIC_SPEED("traffic_speed"),
  SITE_MEASUREMENT("site_measurement"),
  TRAVEL_TIME("travel_time");

  public final String label;

  private Table(String label) {
    this.label = label;
  }
}
