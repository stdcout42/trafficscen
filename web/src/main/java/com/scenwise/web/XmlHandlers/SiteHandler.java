package com.scenwise.web.XmlHandlers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.scenwise.web.Models.SiteMeasurement;


public class SiteHandler extends DefaultHandler {
  private StringBuilder currentValue = new StringBuilder();
  private boolean locForDisplayOpen = false;
  private Instant publicationInstant;
  private List<SiteMeasurement> measurements = new ArrayList<>();
  private SiteMeasurement currMeasurement;


  @Override
  public void startElement(
      String uri,
      String localName,
      String qName,
      Attributes attributes) {

    // reset the tag value
    currentValue.setLength(0);

    if (qName.equalsIgnoreCase("measurementSiteRecord")) {
      String record_site_id = attributes.getValue("id");
      int version = Integer.parseInt(attributes.getValue("version"));
      currMeasurement = new SiteMeasurement(record_site_id, version);
    } else if (qName.equalsIgnoreCase("locationForDisplay")) {
      locForDisplayOpen = true;
    }

  }

  @Override
  public void endElement(String uri,
      String localName,
      String qName) {

    if (qName.equalsIgnoreCase("locationForDisplay")) {
      locForDisplayOpen = false;
    } else if (qName.equalsIgnoreCase("publicationTime")) {
      publicationInstant = Instant.parse(currentValue.toString());
    } else if (locForDisplayOpen && qName.equalsIgnoreCase("latitude")) {
      String latitude = currentValue.toString();
      currMeasurement.setLatitude(Double.parseDouble(latitude));
    } else if (locForDisplayOpen && qName.equalsIgnoreCase("longitude")) {
      String longitude = currentValue.toString();
      currMeasurement.setLongitude(Double.parseDouble(longitude));

    } else if (qName.equalsIgnoreCase("measurementSiteRecord")) {
      assert (locForDisplayOpen == false);
      currMeasurement.setPublicationTime(publicationInstant);
      measurements.add(currMeasurement);
    }

  }


  @Override
  public void characters(char ch[], int start, int length) {
    currentValue.append(ch, start, length);
  }

  public List<SiteMeasurement> getMeasurements() {
    return measurements;
  }

}
