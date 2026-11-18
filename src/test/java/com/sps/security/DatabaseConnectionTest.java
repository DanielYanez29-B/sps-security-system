package com.sps.security;

import com.sps.security.util.DatabaseConnection;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void verificarConexionMySQL() throws Exception {

        try (Connection conexion = DatabaseConnection.getConnection()) {

            assertNotNull(conexion);
            assertFalse(conexion.isClosed());

            try (Statement statement = conexion.createStatement();
                 ResultSet resultado = statement.executeQuery(
                         "SELECT DATABASE()")) {

                assertTrue(resultado.next());
                assertEquals("sps_db", resultado.getString(1));
            }
        }
    }
}
