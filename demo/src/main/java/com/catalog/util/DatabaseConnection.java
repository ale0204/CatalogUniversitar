package com.catalog.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.catalog.exception.CatalogException;

// Singleton: exista o SINGURA instanta in toata aplicatia, deci o singura conexiune JDBC partajata.
// Constructor privat + getInstance() => nimeni din afara nu poate face "new DatabaseConnection()".
public class DatabaseConnection {
    private static final String CONFIG_FILE = "database.properties";

    private static DatabaseConnection instance;

    private final Connection connection;

    private DatabaseConnection() {
        Properties props = incarcaConfig();
        String url      = props.getProperty("db.url");
        String user     = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try {
            this.connection = DriverManager.getConnection(url, user, password);
            // autoCommit=true => fiecare INSERT/UPDATE/DELETE se salveaza imediat.
            // Astfel CatalogApp (care nu apeleaza commit()) ramane neschimbat.
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new CatalogException("Nu m-am putut conecta la baza de date: " + e.getMessage());
        }
    }

    // Citeste database.properties din directorul curent (radacina proiectului),
    // iar daca nu il gaseste acolo, incearca din classpath (resources).
    private Properties incarcaConfig() {
        Properties props = new Properties();
        try (InputStream in = deschideConfig()) {
            props.load(in);
        } catch (IOException e) {
            throw new CatalogException("Nu am putut citi " + CONFIG_FILE + ": " + e.getMessage());
        }
        return props;
    }

    private InputStream deschideConfig() throws IOException {
        java.io.File file = new java.io.File(CONFIG_FILE);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        InputStream fromClasspath = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (fromClasspath != null) {
            return fromClasspath;
        }
        throw new IOException(CONFIG_FILE + " nu a fost gasit nici in directorul curent, nici in classpath.");
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
