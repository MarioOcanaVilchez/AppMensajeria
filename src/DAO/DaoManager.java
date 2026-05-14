package DAO;

import java.sql.*;

public class DaoManager {
    private final String URL;
    private final String USER;
    private final String PASSWORD;
    private Connection conexion;
    private static DaoManager singlenton;

    private DaoManager() {
        conexion = null;
        URL = "jdbc:mysql://localhost:3306/wats";
        USER = "root";
        PASSWORD = "root";
    }
    public Connection getConexion() {
        return conexion;
    }

    public static DaoManager getSinglentonInstance(){
        if (singlenton == null) singlenton = new DaoManager();
        return singlenton;
    }
    public void open() throws SQLException{
        try {
            conexion = DriverManager.getConnection(URL,USER,PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void close(){
        try {
            if (conexion != null) conexion.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
