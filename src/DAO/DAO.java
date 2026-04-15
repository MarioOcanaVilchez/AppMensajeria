package DAO;

import Models.Chat;
import Models.Mensaje;
import Models.User;
import Utils.Utils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DAO {
    public static Connection iniciarConexion(){
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/wats","root","root");
        } catch (SQLException e) {
            return null;
        }
    }
    public static User iniciarSesion(String email,String clave){
        Statement statement;
        email = email.replace('\'',' ');
        clave = clave.replace('\'',' ');
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                statement = conexionBD.createStatement();
                ResultSet resultados = statement.executeQuery("select * from usuariosActivos where email = '" + email + "' and clave = '" + clave + "' order by ultimaConexion desc limit 1");
                resultados.next();
                User user = new User(resultados.getInt("id"),resultados.getString("email"));
                actualizaFecha(user);
                cierraConexion(conexionBD);
                return user;
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
    public static void cierraConexion(Connection conexionBD){
        try {
            conexionBD.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean crearUsuario(String email,String clave){
        String insert = "Insert into usuariosActivos (id,email,clave,ultimaConexion) values (?, ?, ?, ?)";
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                PreparedStatement ps = conexionBD.prepareStatement(insert);
                ps.setInt(1,generaIdActivos());
                ps.setString(2, email);
                ps.setString(3, clave);
                ps.setString(4, Utils.pasaFechaString(LocalDateTime.now()));
                ps.executeUpdate();
                cierraConexion(conexionBD);
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
    public static User buscaUsuarioId(int id){
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                Statement statement = conexionBD.createStatement();
                ResultSet resultados = statement.executeQuery("select * from usuariosActivos where id = '" + id + "' order by ultimaConexion desc limit 1");
                while (resultados.next()) {
                    User user = new User(resultados.getString("email"), resultados.getString("clave"));
                    actualizaFecha(user);
                    cierraConexion(conexionBD);
                    return user;
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
    public static boolean borraUsuario(User user){
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                Statement statement = conexionBD.createStatement();
                ResultSet resultados = statement.executeQuery("select * from usuariosBorrados where id = '" + user.getId() + "' order by ultimaConexion desc limit 1");
                resultados.next();
                String clave = resultados.getString("clave");
                PreparedStatement ps = conexionBD.prepareStatement("delete from usuariosActivos where id = '" + user.getId() + "'");
                ps.executeUpdate();
                ps = conexionBD.prepareStatement("Insert into usuariosBorrados (id,email,clave,ultimaConexion) values(?, ?, ?, ?)");
                ps.setInt(1,generaIdBorrados());
                ps.setString(2, user.getEmail());
                ps.setString(3, clave);
                ps.setString(4, Utils.pasaFechaString(LocalDateTime.now()));
                ps.executeUpdate();
                cierraConexion(conexionBD);
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
    public static void actualizaFecha(User user){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null){
            try {
                PreparedStatement ps = conexionBD.prepareStatement("Update usuariosActivos set ultimaConexion = '" + Utils.pasaFechaString(LocalDateTime.now()) + "' where id ='" + user.getId() + "'");
                ps.executeUpdate();
                cierraConexion(conexionBD);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static int generaIdActivos(){
        int id;
        do{
            id = (int) (Math.random() * 999999);
        }while(buscaUsuarioId(id) != null);
        return id;
    }
    public static int generaIdBorrados(){
        int id;
        do{
            id = (int) (Math.random() * 999999);
        }while(buscaUsuarioBorradoId(id) != null);
        return id;
    }
    public static User buscaUsuarioBorradoId(int id){
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                Statement statement = conexionBD.createStatement();
                ResultSet resultados = statement.executeQuery("select * from usuariosBorrados where id = '" + id + "' order by ultimaConexion desc limit 1");
                while (resultados.next()) {
                    User user = new User(resultados.getString("email"), resultados.getString("clave"));
                    actualizaFecha(user);
                    cierraConexion(conexionBD);
                    return user;
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
    public static User buscaUsuarioEmail(String email){
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                Statement statement = conexionBD.createStatement();
                ResultSet resultados = statement.executeQuery("select * from usuariosActivos where email = '" + email + "' order by ultimaConexion desc limit 1");
                while (resultados.next()) {
                    User user = new User(resultados.getInt("id"),resultados.getString("email"));
                    actualizaFecha(user);
                    cierraConexion(conexionBD);
                    return user;
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
    public static User buscaUsuarioBorradoEmail(String email){
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                Statement statement = conexionBD.createStatement();
                ResultSet resultados = statement.executeQuery("select * from usuariosBorrados where email = '" + email + "' order by ultimaConexion desc limit 1");
                resultados.next();
                User user = new User(resultados.getInt("id"),resultados.getString("email"));
                cierraConexion(conexionBD);
                return user;
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
    public static User recuperaUser(User user,String clave) {
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("delete from usuariosBorrados where id = '" + user.getId() + "'");
                ps.executeUpdate();
                ps = conexionBD.prepareStatement("Insert into usuariosActivos (id,email,clave,ultimaConexion) values (?, ?, ?, ?)");
                ps.setInt(1,generaIdActivos());
                ps.setString(2, user.getEmail());
                ps.setString(3, clave);
                ps.setString(4, Utils.pasaFechaString(LocalDateTime.now()));
                ps.executeUpdate();
                user = buscaUsuarioEmail(user.getEmail());
                actualizaFecha(user);
                cierraConexion(conexionBD);
            } catch (SQLException e) {
                return null;
            }
        }
        return buscaUsuarioEmail(user.getEmail());
    }
    public static void borraUserBorrado(User user) {
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("delete from usuariosBorrados where id = '" + user.getId() + "'");
                ps.executeUpdate();
                cierraConexion(conexionBD);
            } catch (SQLException e) {
                return;
            }
        }
    }
    public static void actualizaUsuario(User user){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null){
            try {
                PreparedStatement ps = conexionBD.prepareStatement("Update usuariosActivos set ultimaConexion = '" + Utils.pasaFechaString(LocalDateTime.now()) + "', email='" + user.getEmail() + "' where id ='" + user.getId() + "'");
                ps.executeUpdate();
                cierraConexion(conexionBD);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static ArrayList<Chat> cargaChats(User user){
        ArrayList<Chat> chats = new ArrayList<>();
        ArrayList<User> usuarios = new ArrayList<>();
        ArrayList<User> admins = new ArrayList<>();
        ArrayList<Mensaje> Mensajes = new ArrayList<>();
        ArrayList<Integer> idChats = new ArrayList<>();
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                Statement statement = conexionBD.createStatement();
                ResultSet resultados = statement.executeQuery("select * from chatUsuario CU inner join chats C on CU.id = C.id where CU.idUser = " + user.getId() + " order by C.ultimoMensaje desc");
                //Cogemos los ids de los chats en los que esta
                while(resultados.next()) {
                    idChats.add(resultados.getInt("id"));
                }
                for (int id: idChats){
                    statement = conexionBD.createStatement();
                    resultados = statement.executeQuery("select * from chatUsuario CU inner join usuariosActivos UA on CU.idUser = UA.id where CU.id = " + id);
                    //Cogemos los usuarios de los chats en los que esta
                    while(resultados.next()) {
                        usuarios.add(new User(resultados.getInt("idUser"),resultados.getString("email")));
                    }
                    statement = conexionBD.createStatement();
                    resultados = statement.executeQuery("select * from userAdmin UAD inner join usuariosActivos UA on UA.id = UAD.idUser where UAD.id = " + id);
                    //Cogemos los usuarios de los chats en los que esta
                    while(resultados.next()) {
                        admins.add(new User(resultados.getInt("idUser"),resultados.getString("email")));
                    }
                    chats.add(new Chat(id,null,usuarios,admins));
                }
                cierraConexion(conexionBD);
            }
        } catch (SQLException e) {
            return null;
        }
        return chats;
    }
}
