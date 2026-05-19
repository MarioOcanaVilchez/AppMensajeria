package DAO;

import Models.Chat;
import Models.Mensaje;
import Models.User;
import Utils.Utils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DaoMensajeSQL {
    public static void addMensaje(Mensaje mensaje, Chat chat,DaoManager dao) {
        long id = generaId(dao);
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("Insert into mensajeChat (id,idChat,idUserEnvia,texto,fechaEnviado) values (?, ?, ?, ?, ?)");
            ps.setLong(1,id);
            ps.setInt(2, chat.getId());
            ps.setInt(3, mensaje.getUsuario().getId());
            ps.setString(4, mensaje.getTexto());
            ps.setString(5, Utils.pasaFechaString(LocalDateTime.now()));
            ps.executeUpdate();
            for (User u : chat.getUsuarios()) {
                ps = dao.getConexion().prepareStatement("Insert into mensajeUsuario (idMensaje,idChat,idUserEnvia,idUserReceptor,texto,fechaEnviado) values (?, ?, ?, ?, ?, ?)");
                ps.setLong(1,id);
                ps.setInt(2, chat.getId());
                ps.setInt(3, mensaje.getUsuario().getId());
                ps.setInt(4,u.getId());
                ps.setString(5, mensaje.getTexto());
                ps.setString(6, Utils.pasaFechaString(LocalDateTime.now()));
                ps.executeUpdate();
            }
            ps = dao.getConexion().prepareStatement("update chats set ultimoMensaje = '" + Utils.pasaFechaString(LocalDateTime.now()) + "' where id = " + chat.getId());
            ps.executeUpdate();
            dao.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<Mensaje> determinaUsuarioEnvia(ArrayList<Mensaje> mensajes,DaoManager dao){
        User usuarioEnvia = new User(0,"Bievenido");
        for (Mensaje m : mensajes){
            if (m.getUsuario().getId() == 0) m.setUsuario(usuarioEnvia);
        }
        for (Mensaje m : mensajes){
            if (m.getUsuario().getEmail() == null){
                usuarioEnvia = DaoUserSQL.buscaUsuarioId(m.getUsuario().getId(),dao);
                if (usuarioEnvia == null){
                    usuarioEnvia = DaoUserSQL.buscaUsuarioBorradoId(m.getUsuario().getId(),dao);
                    if (usuarioEnvia == null){
                        usuarioEnvia = new User(0,"Bievenido");
                    }
                }
                for (Mensaje me : mensajes){
                    if (me.getUsuario().getId() == m.getUsuario().getId()) me.setUsuario(usuarioEnvia);
                }
            }
        }
        return mensajes;
    }

    public static long generaId(DaoManager dao){
        long id;
        do{
            id = (long) (Math.random() * Long.MAX_VALUE);
        }while(buscaMensajeId(id,dao) != null);
        return id;
    }

    public static Mensaje buscaMensajeId(long id,DaoManager dao){
        try {
            dao.open();
            Statement statement = dao.getConexion().createStatement();
            ResultSet rs = statement.executeQuery("select * from mensajeChat where id = '" + id + "' order by ultimaConexion desc limit 1");
            if (rs.next()) {
                Mensaje mensaje = new Mensaje(id,new User(rs.getInt("idUserEnvia")),rs.getString("texto"),-9,Utils.pasarStringFecha(rs.getString("ultimoMensaje")));
                dao.close();
                return mensaje;
            }
            dao.close();
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
    //todo posible problema al crear el primer grupo en caso de no haber tampoco chats
    public static void ponerMensajesLeidos(int idUser,int idChat,DaoManager dao){
        String sentencia = "update mensajeusuario set leido = true where idUserReceptor = " + idUser + " and idChat = " + idChat;
        try {
            dao.open();
            Statement stmt = dao.getConexion().createStatement();
            stmt.executeUpdate(sentencia);
            dao.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
