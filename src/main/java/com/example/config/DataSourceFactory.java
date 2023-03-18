package com.example.config;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSourceFactory {

    private final DataSource dataSource;
    private final Logger LOGGER = Logger.getLogger(DataSourceFactory.class.getName());

    public DataSourceFactory() {
        final PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource() ;
        final String rootPath = Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResource("datasource.properties")
        ).getPath();

        try (InputStream inputStream = new FileInputStream(rootPath)) {
            final Properties properties = new Properties();
            properties.load(inputStream);

            pgSimpleDataSource.setDatabaseName(properties.getProperty("database"));
            pgSimpleDataSource.setServerName(properties.getProperty("serverName"));
            pgSimpleDataSource.setPortNumber(Integer.parseInt(properties.getProperty("port")));
            pgSimpleDataSource.setUser(properties.getProperty("user"));
            pgSimpleDataSource.setPassword(properties.getProperty("password"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO error: ", e);
        }

        this.dataSource = pgSimpleDataSource;
    }

    public static Connection getConnection() throws SQLException {
        return SingletonHelper.INSTANCE.dataSource.getConnection();
    }

    private static class SingletonHelper {
        private static final DataSourceFactory INSTANCE = new DataSourceFactory();
    }
}
