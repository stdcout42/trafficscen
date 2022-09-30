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

import com.scenwise.web.Models.Measurement;

public class MeasurementDAO
    implements TrafficDAO<Measurement> {
  private static final Logger LOGGER = Logger.getLogger(MeasurementDAO.class.getName());
  private final Optional<Connection> connection;

  private final Table table;

  public MeasurementDAO(Table table) {
    this.connection = DBConnection.getConnection();
    this.table = table;
    createTableIfNotExists();
  }



  @Override
  public void batchSave(Collection<Measurement> measurements) {
    final String INSERT_MEASUREMENT_SQL = "INSERT INTO " + table.label +
        " (ref_id, record_version, publication_time, measurement_time, uptodate)" +
        " VALUES (?, ?, ?, ?, ?)";

    connection.ifPresent(conn -> {
      try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT_MEASUREMENT_SQL)) {
        conn.setAutoCommit(false);
        int numInsertions = 0;
        for (Measurement measurement : measurements) {
          preparedStatement.setString(1, measurement.getSiteReferenceId());
          preparedStatement.setInt(2, measurement.getSiteVersion());
          preparedStatement.setTimestamp(3, measurement.getPublicationTime());
          preparedStatement.setTimestamp(4, measurement.getMeasurementTime());
          preparedStatement.setBoolean(5, measurement.isUpToDate());
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
        ResultSet tables = dMetaData.getTables(null, null, table.label, null);
        if (!tables.next()) {
          final String sql = "CREATE TABLE " + table.label +
              "(ref_id varchar(128)," +
              " record_version int," +
              " publication_time timestamp," +
              " measurement_time timestamp," +
              " uptodate boolean," +
              " PRIMARY KEY (ref_id, record_version));";
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
