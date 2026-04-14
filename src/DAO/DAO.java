package DAO;

import Models.User;

import java.sql.*;
import java.time.LocalDateTime;

public class DAO {
    public static Connection iniciarConexion(){
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/wats","root","root");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static User iniciarSesion(Connection conexionBD,String email,String clave){
        Statement statement;
        email = email.replace('\'',' ');
        clave = clave.replace('\'',' ');
        try {
            statement = conexionBD.createStatement();
            ResultSet resultados = statement.executeQuery("select * from usuariosActivos where email = '" + email + "' and clave = '" + clave + "' order by ultimaConexion desc limit 1");
            while(resultados.next()){
                return new User(resultados.getString("email"),resultados.getString("clave"));
            }
        } catch (SQLException e) {
            cierraConexion(conexionBD);
        }
        cierraConexion(conexionBD);
        return null;
    }
    public static void cierraConexion(Connection conexionBD){
        try {
            conexionBD.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean crearUsuario(Connection conexionBD,String email,String clave){
        String insert = "Insert into usuariosActivos (email,clave) values (?, ?)";
        try {
            PreparedStatement ps = conexionBD.prepareStatement(insert);
            ps.setString(1,email);
            ps.setString(2,clave);
            ps.executeUpdate();
            cierraConexion(conexionBD);
            return true;
        } catch (SQLException e) {
            cierraConexion(conexionBD);
            return false;
        }
    }
}
