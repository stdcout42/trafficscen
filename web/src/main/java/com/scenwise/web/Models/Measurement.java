package com.scenwise.web.Models;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Model for traffic speed / travel time.
 * For our purposes these two have identical fields.
 */
public class Measurement {
  public static final String TRAFFIC_SPEED_PATH = "src/main/resources/xml/trafficspeed.xml";
  public static final String TRAVEL_TIME_PATH = "src/main/resources/xml/traveltime.xml";
  private String siteReferenceId;
  private int siteVersion;
  private Timestamp publicationTime;
  private Timestamp measurementTime;
  private boolean isUpToDate;

  /**
   * 
   * @param siteReferenceId: site location id
   * @param siteVersion site location version
   * @param publicationInstant time of publication
   * @param measurementInstant (default) time measurement
   */
  public Measurement(
      String siteReferenceId,
      int siteVersion,
      Instant publicationInstant,
      Instant measurementInstant) {

    this.siteReferenceId = siteReferenceId;
    this.siteVersion = siteVersion;
    this.publicationTime = Timestamp.from(publicationInstant);
    this.measurementTime = Timestamp.from(measurementInstant);
    long deltaSec = measurementInstant.getEpochSecond() - publicationInstant.getEpochSecond();
    this.isUpToDate = (deltaSec / 60.0) < 15.0;
  }

  public Measurement(String refId, int version) {
    this.siteReferenceId = refId;
    this.siteVersion = version;
  }

  public String getSiteReferenceId() {
    return siteReferenceId;
  }

  public void setSiteReference(String siteReference) {
    this.siteReferenceId = siteReference;
  }

  public int getSiteVersion() {
    return siteVersion;
  }

  public void setSiteVersion(int siteVersion) {
    this.siteVersion = siteVersion;
  }

  public Timestamp getPublicationTime() {
    return publicationTime;
  }

  public void setPublicationTime(Instant publicationInstant) {
    this.publicationTime = Timestamp.from(publicationInstant);
  }

  public Timestamp getMeasurementTime() {
    return measurementTime;
  }

  public void setMeasurementTime(Instant instant) {
    this.measurementTime = Timestamp.from(instant);
  }

  public boolean isUpToDate() {
    return isUpToDate;
  }

  public void setUpToDate(boolean isUpToDate) {
    this.isUpToDate = isUpToDate;
  }

  public void computeUptToDate() {
    Long deltaTime = publicationTime.toInstant().getEpochSecond() -
        measurementTime.toInstant().getEpochSecond();
    this.isUpToDate = (deltaTime / 60.0) < 15.0;
  }

}
