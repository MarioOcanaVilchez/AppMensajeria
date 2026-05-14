package DAO;

import Models.Chat;
import Models.Mensaje;
import Models.User;
import Utils.Utils;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DaoMensajeSQL {
    public static void addMensaje(Mensaje mensaje, Chat chat,DaoManager dao) {
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("Insert into mensajeChat (idChat,idUserEnvia,texto,fechaEnviado) values (?, ?, ?, ?)");
            ps.setInt(1, chat.getId());
            ps.setInt(2, mensaje.getUsuario().getId());
            ps.setString(3, mensaje.getTexto());
            ps.setString(4, Utils.pasaFechaString(LocalDateTime.now()));
            ps.executeUpdate();
            for (User u : chat.getUsuarios()) {
                ps = dao.getConexion().prepareStatement("Insert into mensajeUsuario (idChat,idUserEnvia,idUserReceptor,texto,fechaEnviado) values (?, ?, ?, ?,?)");
                ps.setInt(1, chat.getId());
                ps.setInt(2, mensaje.getUsuario().getId());
                ps.setInt(3,u.getId());
                ps.setString(4, mensaje.getTexto());
                ps.setString(5, Utils.pasaFechaString(LocalDateTime.now()));
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
}
