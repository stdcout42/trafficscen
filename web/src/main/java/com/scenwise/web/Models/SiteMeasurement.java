package com.scenwise.web.Models;

import java.sql.Timestamp;
import java.time.Instant;

public class SiteMeasurement {
  private String recordId;
  private int recordVersion;
  private double latitude;
  private double longitude;
  private Timestamp publicationTime;
  private boolean inUtrecht;

  public static String filepath = "src/main/resources/xml/measurement.xml";

  public SiteMeasurement(
      String recordId,
      int recordVersion,
      double d,
      double e,
      Instant publicationTime,
      boolean inUtrecht) {
    this.recordId = recordId;
    this.recordVersion = recordVersion;
    this.latitude = d;
    this.longitude = e;
    this.publicationTime = Timestamp.from(publicationTime);
    this.inUtrecht = inUtrecht;
  }

  public SiteMeasurement(
      String recordId,
      int recordVersion,
      Timestamp publicationTime) {
    this.recordId = recordId;
    this.recordVersion = recordVersion;
    this.publicationTime = publicationTime;
  }

  public SiteMeasurement() {
  }

  public SiteMeasurement(String recordId, int recordVersion) {
    this.recordId = recordId;
    this.recordVersion = recordVersion;
  }

  public String getRecordId() {
    return recordId;
  }

  public int getRecordVersion() {
    return recordVersion;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public Timestamp getPublicationTime() {
    return publicationTime;
  }

  public boolean isInUtrecht() {
    return inUtrecht;
  }

  public void setRecordId(String recordId) {
    this.recordId = recordId;
  }

  public void setRecordVersion(int recordVersion) {
    this.recordVersion = recordVersion;
  }

  public void setLatitude(double d) {
    this.latitude = d;
  }

  public void setLongitude(double d) {
    this.longitude = d;
  }

  public void setPublicationTime(Instant publicationTime) {
    this.publicationTime = Timestamp.from(publicationTime);
  }

  public void setInUtrecht(boolean inUtrecht) {
    this.inUtrecht = inUtrecht;
  }

}
