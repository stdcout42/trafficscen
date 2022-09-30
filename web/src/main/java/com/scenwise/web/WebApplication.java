package com.scenwise.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scenwise.web.DB.JsonCache;
import com.scenwise.web.DB.SiteMeasurementDAO;
import com.scenwise.web.DB.Table;
import com.scenwise.web.Models.SiteMeasurement;

@SpringBootApplication
@RestController
public class WebApplication {
	private JsonCache jsonCache = null;
	private static final Logger LOGGER = Logger.getLogger(SiteMeasurementDAO.class.getName());
	private static final String needUpdateFile = "src/main/resources/update";

	public static void main(String[] args) {
		setUpWebLogger();
		updateIfNeeded();
		SpringApplication.run(WebApplication.class, args);
	}

	private static void updateIfNeeded() {
		boolean requiresUpdate = false;
		try (Scanner scanner = new Scanner(Paths.get(needUpdateFile))) {
			String needUpString = scanner.nextLine();
			if (needUpString.strip().toLowerCase().equals("true")) {
				requiresUpdate = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (requiresUpdate) {
			try {
				Files.write(Paths.get(needUpdateFile), "false".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			parseAllAndCreateTables();			
		}
	}

	@GetMapping(value = "/getResults", produces = MediaType.APPLICATION_JSON_VALUE)
	public String results() {
		LOGGER.info("GET getResults");
		if (jsonCache != null && !jsonCache.isCacheStale()) {
			LOGGER.info("Serving from cache.");
			return jsonCache.getCache();
		}

		SiteMeasurementDAO siteMeasurementDAO = new SiteMeasurementDAO();
		Optional<String> json = siteMeasurementDAO.getCombinedJsonStr();
		if (json.isPresent() && !json.isEmpty()) {
			Long startTime = System.currentTimeMillis();
			jsonCache = new JsonCache(json.get());
			LOGGER.info(Long.toString(System.currentTimeMillis() - startTime) + "ms to query.");
			return json.get();
		}
		return "{\"status\": \"oops\"}";
	}

	

	private static void parseAllAndCreateTables() {
		Long startTime = System.currentTimeMillis();
		TrafficDataManager.parseAndInsertSiteMeasurements(SiteMeasurement.filepath);
		TrafficDataManager.parseAndInsertMeasurements(Table.TRAFFIC_SPEED);
		TrafficDataManager.parseAndInsertMeasurements(Table.TRAVEL_TIME);
		LOGGER.info(Long.toString(System.currentTimeMillis() - startTime) + "ms for initial setup");
	}

	private static void setUpWebLogger() {
		try {
			FileHandler fileHandler = new FileHandler("server_logs.log");
			LOGGER.addHandler(fileHandler);
			fileHandler.setFormatter(new SimpleFormatter());
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

}
