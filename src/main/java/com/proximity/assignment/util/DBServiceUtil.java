package com.proximity.assignment.util;

import com.proximity.assignment.api.PropertiesService;
import com.proximity.assignment.commons.Constants;
import com.proximity.assignment.commons.DBResult;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author sthammishetty on 18/06/21
 */
public class DBServiceUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceUtil.class);
    private static HikariDataSource hikariDataSource = null;

    public static synchronized void initializeHikariDataSource() {
        //TODO need to think about SSL
        if (hikariDataSource == null) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(PropertiesService.getValue(Constants.DB_URL, "jdbc:postgresql://localhost:5432/postgres"));
            hikariConfig.setUsername(PropertiesService.getValue(Constants.DB_USERNAME, "postgres"));
            hikariConfig.setPassword(PropertiesService.getValue(Constants.DB_PASSWORD, ""));
            hikariConfig.setDriverClassName(PropertiesService.getValue(Constants.DB_DRIVER_CLASS_NAME, "org.postgresql.Driver"));
            hikariConfig.setMaximumPoolSize(Integer.valueOf(PropertiesService.getValue(Constants.DB_CONNECTION_POOL_SIZE, "5")));
            hikariConfig.setPoolName("hikariCP-Pool");
            hikariConfig.setInitializationFailTimeout(3000);
            hikariConfig.setConnectionTestQuery("SELECT 1");
            hikariDataSource = new HikariDataSource(hikariConfig);
        }
    }

    public static void closeDbConnections() {
        if (hikariDataSource != null) {
            try {
                hikariDataSource.close();
            } catch (Exception e) {
                LOGGER.error("Error while shutting down hikari dataSource.", e);
            }
        }
    }

    public static DBResult executeQuery(String query) {
        DBResult dbResult = new DBResult();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement statement = connection.prepareStatement(query);
            ResultSet resultSet = ((PreparedStatement) statement).executeQuery();
            dbResult.setResult(getResult(resultSet));
            dbResult.setSuccess(true);
        } catch (Exception e) {
            handleQueryExecutionFailure(dbResult, e);
        } finally {
            closeConnection(connection);
        }

        return dbResult;
    }

    public static DBResult executeUpdate(String query) {
        DBResult dbResult = new DBResult();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement statement = connection.prepareStatement(query);
            dbResult.setResult(((PreparedStatement) statement).executeUpdate());
            dbResult.setSuccess(true);
        } catch (Exception e) {
            handleQueryExecutionFailure(dbResult, e);
        } finally {
            closeConnection(connection);
        }

        return dbResult;
    }


    public static DBResult executeUpdateWithParams(String query, JSONArray parameters) {
        DBResult dbResult = new DBResult();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement prepStmt = connection.prepareStatement(query);
            for (int i = 0; i < parameters.length(); i++) {
                if (parameters.isNull(i)) {
                    prepStmt.setObject(i + 1, null);
                } else {
                    prepStmt.setObject(i + 1, parameters.get(i));
                }
            }
            int result = prepStmt.executeUpdate();
            dbResult.setSuccess(true);
            dbResult.setResult(result);
        } catch (Exception e) {
            handleQueryExecutionFailure(dbResult, e);
        } finally {
            closeConnection(connection);
        }

        return dbResult;
    }

    private static void handleQueryExecutionFailure(DBResult dbResult, Exception e) {
        LOGGER.error("Error executing dbQuery", e);
        dbResult.setSuccess(false);
        dbResult.setCause(e);
    }


    private static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("Error closing db connection", e);
            }
        }
    }


    private static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = hikariDataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Error fetching db connection", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error fetching db connection", e);
            throw new SQLException(e);
        }
        return connection;
    }


    private static JSONArray getResult(ResultSet resultSet) throws SQLException {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < columnCount; ++i) {
                Object object = resultSet.getObject(i + 1);
                if (object == null) {
                    object = JSONObject.NULL;
                }
                jsonObject.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), object);
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }
}
