package com.scenwise.web;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.scenwise.web.Models.SiteMeasurement;


public class ReverseGeoCoder {
  private static final Logger LOGGER = Logger.getLogger(ReverseGeoCoder.class.getName());

  public static void process(List<SiteMeasurement> measurements) {
    int numLookups = 0;
    for (SiteMeasurement measurement : measurements) {
      try {
        String latitude = Double.toString(measurement.getLatitude());
        String longitude = Double.toString(measurement.getLongitude());
        DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse("http://172.17.0.1:9999/reverse.php?lat="
        // Document doc = documentBuilder.parse("http://localhost:8088/reverse.php?lat="
            + latitude + "&lon=" + longitude);
        doc.getDocumentElement().normalize();
        Element element = (Element) doc.getElementsByTagName("state").item(0);
        if (element != null) {
          measurement.setInUtrecht(true);
        } else {
          measurement.setInUtrecht(false);
        }

        if (++numLookups % 1000 == 0) {
          LOGGER.info(numLookups + " lookups done.");
        }
      } catch (IOException | SAXException | ParserConfigurationException e) {
        e.printStackTrace();
      }
    }
  }

}
