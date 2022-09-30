package com.scenwise.web.DB;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.scenwise.web.Models.SiteMeasurement;

/**
 * DB access for the site locations
 */
public class SiteMeasurementDAO implements TrafficDAO<SiteMeasurement> {
  private static final Logger LOGGER = Logger.getLogger(SiteMeasurementDAO.class.getName());
  private final Optional<Connection> connection;

  public SiteMeasurementDAO() {
    this.connection = DBConnection.getConnection();
    createTableIfNotExists();
  }

  public Optional<SiteMeasurement> get(String site_record_id, int record_version) {
    return connection.flatMap(conn -> {
      final String sql = "SELECT * FROM " + Table.SITE_MEASUREMENT + " WHERE site_record_id = ?" +
          " AND record_version = ?";

      Optional<SiteMeasurement> measurement = Optional.empty();

      try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
        preparedStatement.setString(1, site_record_id);
        preparedStatement.setInt(2, record_version);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
          measurement = Optional.of(new SiteMeasurement(
              resultSet.getString("site_record_id"),
              resultSet.getInt("record_version"),
              resultSet.getDouble("latitude"),
              resultSet.getDouble("longitude"),
              resultSet.getTimestamp("publication_time").toInstant(),
              resultSet.getBoolean("utrecht")));
        }

      } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, null, e);
      }
      return measurement;

    });
  }

  public Optional<String> getCombinedJsonStr() {
    return connection.flatMap(conn -> {
      final String sql = "select json_agg(res) from (" +
          "select sm.site_record_id as siteId, sm.utrecht as inUtrecht," +
          " sm.latitude as lat, sm.longitude as lon," +
          " ts.uptodate as speedUpToDate," +
          " tt.uptodate as travelTimeUpToDate," +
          " ts.publication_time as speedPubTime," +
          " tt.publication_time as travelPubTime " +
          " from site_measurement sm" +
          " left join traffic_speed ts on (ts.ref_id like sm.site_record_id)" +
          " left join travel_time tt on (tt.ref_id like sm.site_record_id)" +
          " ) res;";

      Optional<String> combinedJsonStr = Optional.empty();
      try (Statement statement = conn.createStatement()) {
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
          String jsonStr = resultSet.getString(1);
          if (jsonStr != null) {
            combinedJsonStr = Optional.of(resultSet.getString(1));
          }
        }

      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, null, e);
      }
      return combinedJsonStr;
    });

  }

  public void deleteAll() {
    connection.ifPresent(conn -> {
      try (Statement statement = conn.createStatement()) {
        final String DELETE_ALL_SQL = "DELETE FROM " + Table.SITE_MEASUREMENT;
        statement.executeUpdate(DELETE_ALL_SQL);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });

  }

  @Override
  public void batchSave(Collection<SiteMeasurement> measurements) {
    final String INSERT_MEASUREMENT_SQL = "INSERT INTO " + Table.SITE_MEASUREMENT +
        " (site_record_id, record_version, latitude, longitude, publication_time, utrecht)" +
        " VALUES (?, ?, ?, ?, ?, ?)";

    connection.ifPresent(conn -> {
      try (
          PreparedStatement preparedStatement = conn.prepareStatement(INSERT_MEASUREMENT_SQL)) {

        conn.setAutoCommit(false);
        int numInsertions = 0;
        for (SiteMeasurement measurement : measurements) {
          preparedStatement.setString(1, measurement.getRecordId());
          preparedStatement.setInt(2, measurement.getRecordVersion());
          preparedStatement.setDouble(3, measurement.getLatitude());
          preparedStatement.setDouble(4, measurement.getLongitude());
          preparedStatement.setTimestamp(5, measurement.getPublicationTime());
          preparedStatement.setBoolean(6, measurement.isInUtrecht());
          preparedStatement.addBatch();

          if (++numInsertions % 500 == 0 || numInsertions == measurements.size()) {
            preparedStatement.executeBatch();
            LOGGER.info(numInsertions + " added to execution plan.");
          }
        }

        conn.commit();
        conn.setAutoCommit(true);

      } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, null, e);
      }
    });

  }

  @Override
  public void createTableIfNotExists() {
    connection.ifPresent(conn -> {
      DatabaseMetaData dMetaData;
      try {
        dMetaData = conn.getMetaData();
        ResultSet tables = dMetaData.getTables(null, null, Table.SITE_MEASUREMENT.label, null);
        if (!tables.next()) {
          final String sql = "CREATE TABLE " + Table.SITE_MEASUREMENT.label +
              "(site_record_id varchar(128)," +
              " record_version int," +
              " latitude double precision," +
              " longitude double precision," +
              " publication_time timestamp," +
              " utrecht boolean," +
              " PRIMARY KEY (site_record_id, record_version));";
          try (Statement statement = conn.createStatement()) {
            statement.execute(sql);
          } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
          }
        }

      } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, null, e);
      }
    });
  }
}