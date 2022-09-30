package com.scenwise.web;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.scenwise.web.DB.MeasurementDAO;
import com.scenwise.web.DB.SiteMeasurementDAO;
import com.scenwise.web.DB.Table;
import com.scenwise.web.Models.Measurement;
import com.scenwise.web.Models.SiteMeasurement;
import com.scenwise.web.XmlHandlers.MeasurementHandler;
import com.scenwise.web.XmlHandlers.SiteHandler;



public class TrafficDataManager {

  public static void parseAndInsertSiteMeasurements(String filepath) {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      // XXE attack, see https://rules.sonarsource.com/java/RSPEC-2755
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

      SAXParser saxParser = factory.newSAXParser();
      SiteHandler measurementHandler = new SiteHandler();
      saxParser.parse(filepath, measurementHandler);
      List<SiteMeasurement> measurements = measurementHandler.getMeasurements();
      ReverseGeoCoder.process(measurements);
      SiteMeasurementDAO measurementDAO = new SiteMeasurementDAO();
      measurementDAO.batchSave(measurements);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
  }

  public static void parseAndInsertMeasurements(Table table) {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      // XXE attack, see https://rules.sonarsource.com/java/RSPEC-2755
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

      SAXParser saxParser = factory.newSAXParser();
      MeasurementHandler handler = new MeasurementHandler();
      final String filepath = table.equals(Table.TRAFFIC_SPEED) ? Measurement.TRAFFIC_SPEED_PATH
          : Measurement.TRAVEL_TIME_PATH;
      saxParser.parse(filepath, handler);
      List<Measurement> measurements = handler.getMeasurements();
      MeasurementDAO tSpeedMeasurementDAO = new MeasurementDAO(table);
      tSpeedMeasurementDAO.batchSave(measurements);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
  }
}