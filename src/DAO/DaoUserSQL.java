package DAO;

import Models.Chat;
import Models.User;
import Utils.Utils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DaoUserSQL {
    public static User iniciarSesion(String email, String clave,DaoManager dao){
        Statement statement;
        email = email.replace('\'',' ');
        clave = clave.replace('\'',' ');
        try {
            dao.open();
            statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from usuariosActivos where email = '" + email + "' and clave = '" + clave + "' order by ultimaConexion desc limit 1");
            resultados.next();
            User user = new User(resultados.getInt("id"),resultados.getString("email"));
            dao.close();
            actualizaFecha(user,dao);
                return user;
        } catch (SQLException e) {
            return null;
        }
    }
    public static void actualizaFecha(User user,DaoManager dao){
            try {
                dao.open();
                PreparedStatement ps = dao.getConexion().prepareStatement("Update usuariosActivos set ultimaConexion = '" + Utils.pasaFechaString(LocalDateTime.now()) + "' where id ='" + user.getId() + "'");
                ps.executeUpdate();
                dao.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    public static User buscaUsuarioId(int id,DaoManager dao){
        try {
            dao.open();
            Statement statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from usuariosActivos where id = '" + id + "' order by ultimaConexion desc limit 1");
            if (resultados.next()) {
                User user = new User(id,resultados.getString("email"));
                dao.close();
                return user;
                }
            dao.close();
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

     public static boolean crearUsuario(String email,String clave,DaoManager dao){
        String insert = "Insert into usuariosActivos (id,email,clave,ultimaConexion) values (?, ?, ?, ?)";
        int id = generaId(dao);
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement(insert);
            ps.setInt(1,id);
            ps.setString(2, email);
            ps.setString(3, clave);
            ps.setString(4, Utils.pasaFechaString(LocalDateTime.now()));
            ps.executeUpdate();
            dao.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static int generaId(DaoManager dao){
        int id;
        do{
            id = (int) (Math.random() * 9999998 + 1);
        }while(buscaUsuarioId(id,dao) != null && buscaUsuarioBorradoId(id,dao) != null);
        return id;
    }
    public static User recuperaUser(User user,String clave,DaoManager dao) {
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("delete from usuariosBorrados where id = " + user.getId());
            ps.executeUpdate();
            ps = dao.getConexion().prepareStatement("Insert into usuariosActivos (id,email,clave,ultimaConexion) values (?, ?, ?, ?)");
            ps.setInt(1,user.getId());
            ps.setString(2, user.getEmail());
            ps.setString(3, clave);
            ps.setString(4, Utils.pasaFechaString(LocalDateTime.now()));
            ps.executeUpdate();
            dao.close();
            user = buscaUsuarioEmail(user.getEmail(),dao);
            //recuperarChats
            if (user != null){
                DaoChatSQL.recuperarChats(user, dao);
                actualizaFecha(user,dao);
            }
            return user;
        } catch (SQLException e) {
            return null;
        }
    }

    public static void borraUserBorrado(User user,DaoManager dao) {
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("delete from usuariosBorrados where id = '" + user.getId() + "'");
            ps.executeUpdate();
            dao.close();
        } catch (SQLException e) {
            return;
        }
    }
    public static User buscaUsuarioEmail(String email,DaoManager dao){
        try {
            dao.open();
            Statement statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from usuariosActivos where email = '" + email + "' order by ultimaConexion desc limit 1");
            if (resultados.next()) {
                User user = new User(resultados.getInt("id"),resultados.getString("email"));
                dao.close();
                actualizaFecha(user,dao);
                return user;
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public static boolean usuarioBloqueado(User bloqueado, User bloquea,DaoManager dao) {
        String sentencia = "select * from usuarioBloqueado where idUserBloquea =" + bloquea.getId() + " and idUserBloqueado =" + bloqueado.getId();
        try {
            dao.open();
            Statement stmt = dao.getConexion().createStatement();
            ResultSet rs = stmt.executeQuery(sentencia);
            if (rs.next()){
                dao.close();
                return true;
            }
            dao.close();
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean bloqueaUser(User bloqueado, User bloquea,DaoManager dao) {
        String sentencia = "insert into usuarioBloqueado values (" + bloquea.getId() + "," + bloqueado.getId() + ")";
        try {
            dao.open();
            Statement stmt = dao.getConexion().createStatement();
            stmt.executeUpdate(sentencia);
            dao.close();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean borraUsuario(User user, ArrayList<Chat> chats, DaoManager dao){
        try {
            int id = user.getId();
            dao.open();
            Statement statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from usuariosActivos where id = " + user.getId() + " order by ultimaConexion desc limit 1");
            resultados.next();
            String clave = resultados.getString("clave");
            PreparedStatement ps = dao.getConexion().prepareStatement("delete from usuariosActivos where id = " + user.getId());
            ps.executeUpdate();
            ps = dao.getConexion().prepareStatement("Insert into usuariosBorrados (id,email,clave,ultimaConexion) values(?, ?, ?, ?)");
            ps.setInt(1,id);
            ps.setString(2, user.getEmail());
            ps.setString(3, clave);
            ps.setString(4, Utils.pasaFechaString(LocalDateTime.now()));
            ps.executeUpdate();
            for (Chat chat : chats){
                if (!chat.isChat()){
                    DaoChatSQL.eliminaGrupo(chat,user,dao);
                    DaoChatSQL.addMiembroAnt(chat,user,dao);
                } else DaoChatSQL.eliminaChat(chat,user,dao);
            }
            dao.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public static User buscaUsuarioBorradoId(int id,DaoManager dao){
        try {
            User user = null;
            dao.open();
            Statement statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from usuariosBorrados where id = '" + id + "' order by ultimaConexion desc limit 1");
            if (resultados.next()) {
                 user = new User(id, resultados.getString("email"));
                actualizaFecha(user, dao);
            }
            dao.close();
            return user;
        } catch (SQLException e) {
            return null;
        }
    }
    public static User buscaUsuarioBorradoEmail(String email,DaoManager dao){
        email = email.replace('\'',' ');
        User user = null;
        try {
            dao.open();
            Statement statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from usuariosBorrados where email = '" + email + "' order by ultimaConexion desc limit 1");
            if (resultados.next()) user = new User(resultados.getInt("id"), resultados.getString("email"));
            dao.close();
            return user;
        } catch (SQLException e) {
            return null;
        }
    }


    public static void actualizaUsuario(User user,DaoManager dao){
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("Update usuariosActivos set ultimaConexion = '" + Utils.pasaFechaString(LocalDateTime.now()) + "', email='" + user.getEmail() + "' where id ='" + user.getId() + "'");
            ps.executeUpdate();
            dao.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean cambiarEmailUsuario(User user, String nombre,DaoManager dao){
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("update usuariosActivos set email ='" + nombre + "' where id = " + user.getId());
            ps.executeUpdate();
            dao.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public static boolean cambiarClaveUsuario(User user, String clave,DaoManager dao){
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("update usuariosActivos set clave ='" + clave + "' where id = " + user.getId());
            ps.executeUpdate();
            dao.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}

