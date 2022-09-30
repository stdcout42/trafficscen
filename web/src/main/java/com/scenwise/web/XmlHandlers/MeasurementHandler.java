package com.scenwise.web.XmlHandlers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.scenwise.web.Models.Measurement;


public class MeasurementHandler extends DefaultHandler {
  private StringBuilder currentValue = new StringBuilder();
  private Instant publicationInstant;
  private List<Measurement> measurements = new ArrayList<>();
  private Measurement currTrafficSpeedData;

  public void startElement(
      String uri,
      String localName,
      String qName,
      Attributes attributes) {

    // reset the tag value
    currentValue.setLength(0);

    if (qName.equalsIgnoreCase("measurementSiteReference")) {
      String refId = attributes.getValue("id");
      int version = Integer.parseInt(attributes.getValue("version"));
      currTrafficSpeedData = new Measurement(refId, version);
    }
  }

  @Override
  public void endElement(String uri,
      String localName,
      String qName) {

    if (qName.equalsIgnoreCase("publicationTime")) {
      publicationInstant = Instant.parse(currentValue.toString());
    } else if (qName.equalsIgnoreCase("measurementTimeDefault")) {
      currTrafficSpeedData.setMeasurementTime(Instant.parse(currentValue.toString()));
    } else if (qName.equalsIgnoreCase("siteMeasurements")) {
      currTrafficSpeedData.setPublicationTime(publicationInstant);
      currTrafficSpeedData.computeUptToDate();
      measurements.add(currTrafficSpeedData);
    }
  }

  @Override
  public void characters(char ch[], int start, int length) {
    currentValue.append(ch, start, length);
  }

  public List<Measurement> getMeasurements() {
    return measurements;
  }

}
