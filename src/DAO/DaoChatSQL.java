package DAO;

import Models.Chat;
import Models.Mensaje;
import Models.User;
import Utils.Utils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DaoChatSQL {
    public static ArrayList<Chat> cargaChats(User user,DaoManager dao){
        ArrayList<Chat> chats = new ArrayList<>();
        ArrayList<Integer> idChats = new ArrayList<>();
        try {
            dao.open();
            Statement statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from chatUsuario CU inner join chats C on CU.id = C.id where CU.idUser = " + user.getId() + " order by C.ultimoMensaje desc");
            //Cogemos los ids de los chats en los que esta
            while(resultados.next()) {
                idChats.add(resultados.getInt("id"));
            }
            dao.close();
            for (int id: idChats){
                dao.open();
                ArrayList<User> usuarios = new ArrayList<>();
                ArrayList<User> admins = new ArrayList<>();
                String nombre;
                LocalDateTime fechaUltimoMensaje;
                boolean chat;
                int mensajesNoLeidos;
                statement = dao.getConexion().createStatement();
                resultados = statement.executeQuery("select * from chatUsuario CU inner join usuariosActivos UA on CU.idUser = UA.id where CU.id = " + id);
                //Cogemos los usuarios de los chats en los que esta
                while(resultados.next()) {
                    usuarios.add(new User(resultados.getInt("idUser"),resultados.getString("email")));
                }
                statement = dao.getConexion().createStatement();
                resultados = statement.executeQuery("select * from userAdmin UAD inner join usuariosActivos UA on UA.id = UAD.idUser where UAD.id = " + id);//Cogemos los usuarios de los chats en los que esta
                while(resultados.next()) {
                    admins.add(new User(resultados.getInt("idUser"),resultados.getString("email")));
                }
                statement = dao.getConexion().createStatement();
                resultados = statement.executeQuery("select * from chats where id = " + id + " order by ultimoMensaje desc limit 1");
                //Cogemos los usuarios de los chats en los que esta
                resultados.next();
                nombre = resultados.getString("nombre");
                fechaUltimoMensaje = Utils.pasarStringFecha(resultados.getString("ultimoMensaje"));
                chat = resultados.getBoolean("chat");
                dao.close();
                mensajesNoLeidos = determinarMensajesSinLeer(id, user.getId(), dao);
                chats.add(new Chat(id,null,usuarios,admins,nombre,user,fechaUltimoMensaje,mensajesNoLeidos,chat));

            }

        } catch (SQLException e) {
            return null;
        }
        return chats;
    }
    public static int determinarMensajesSinLeer(int idChat,int idUser,DaoManager dao){
        String sentencia = "select count(*) from mensajeUsuario where idChat = " + idChat + " and idUserReceptor = " + idUser + " and leido = false order by fechaEnviado desc";
        int mensajesNoLeidos;
        try {
            dao.open();
            Statement stmt = dao.getConexion().createStatement();
            ResultSet rs = stmt.executeQuery(sentencia);
            rs.next();
            mensajesNoLeidos = rs.getInt("count(*)");
            dao.close();
            return mensajesNoLeidos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Chat cargaChat(User user,int idChat,DaoManager dao){
        Chat chat;
        try {
            dao.open();
            ArrayList<User> usuarios = new ArrayList<>();
            ArrayList<User> admins = new ArrayList<>();
            ArrayList<Mensaje> mensajes = new ArrayList<>();
            LocalDateTime fechaUltimoMensaje;
            String nombre;
            boolean esChat;
            int mensajesNoLeidos;
            Statement statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from chatUsuario CU inner join usuariosActivos UA on CU.idUser = UA.id where CU.id = " + idChat);
            //Cogemos los usuarios de los chats en los que esta
            while(resultados.next()) {
                usuarios.add(new User(resultados.getInt("idUser"),resultados.getString("email")));
            }
            statement = dao.getConexion().createStatement();
            resultados = statement.executeQuery("select * from userAdmin UAD inner join usuariosActivos UA on UA.id = UAD.idUser where UAD.id = " + idChat);
            //Cogemos los usuarios de los chats en los que esta
            while(resultados.next()) {
                admins.add(new User(resultados.getInt("idUser"),resultados.getString("email")));
            }
            statement = dao.getConexion().createStatement();
            resultados = statement.executeQuery("select * from mensajeUsuario where idChat = " + idChat + " and idUserReceptor = " + user.getId() + " order by fechaEnviado asc");
            //Cogemos los usuarios de los chats en los que esta
            while(resultados.next()) {
                mensajes.add(new Mensaje(resultados.getLong("idMensaje"),new User(resultados.getInt("idUserEnvia")),resultados.getString("texto"),idChat,Utils.pasarStringFecha(resultados.getString("fechaEnviado"))));
            }
            statement = dao.getConexion().createStatement();
            resultados = statement.executeQuery("select * from chats where id = " + idChat + " order by ultimoMensaje desc limit 1");
            //Cogemos los usuarios de los chats en los que esta
            resultados.next();
            nombre = resultados.getString("nombre");
            fechaUltimoMensaje = Utils.pasarStringFecha(resultados.getString("ultimoMensaje"));
            esChat = resultados.getBoolean("chat");
            dao.close();
            mensajes = DaoMensajeSQL.determinaUsuarioEnvia(mensajes,dao);
            mensajesNoLeidos = determinarMensajesSinLeer(idChat, user.getId(), dao);
            chat = new Chat(idChat,mensajes,usuarios,admins,nombre,user,fechaUltimoMensaje,mensajesNoLeidos,esChat);
        } catch (SQLException e) {
            return null;
        }
        return chat;
    }
    public static String generaNombreChat(ArrayList<User> users) {
        String nombre = "Chat de ";
        for (int i = 0; i < users.size(); i++) {
            if (i != users.size() - 1) nombre += users.get(i).getEmail() + ", ";
            else nombre += users.get(i).getEmail();
        }
        return nombre;
    }
    public static Chat crearGrupo(ArrayList<User>usuarios,User uTemp,String nombre,User user,DaoManager dao){
        int id = generaIdChat(dao);
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("Insert into chats (id,nombre,ultimoMensaje,chat) values (?, ?, ?, ?)");
            ps.setInt(1, id);
            if (nombre == null) nombre = generaNombreChat(usuarios);
            ps.setString(2,nombre);
            ps.setString(3, Utils.pasaFechaString(LocalDateTime.now()));
            ps.setBoolean(4,false);
            ps.executeUpdate();
            for (User u : usuarios){
                ps = dao.getConexion().prepareStatement("Insert into chatUsuario (id,idUser) values (?, ?)");
                ps.setInt(1, id);
                ps.setInt(2, u.getId());
                ps.executeUpdate();
            }
            if (uTemp != null) {
                ps = dao.getConexion().prepareStatement("Insert into userAdmin (id,idUser) values (?, ?)");
                ps.setInt(1, id);
                ps.setInt(2, uTemp.getId());
                ps.executeUpdate();
            }
            dao.close();
            return new Chat(id,usuarios,uTemp,nombre,user,0,false);
        }catch (SQLException e) {
            return null;
        }
    }
    public static Chat crearChat(ArrayList<User>usuarios,User uTemp,User user,DaoManager dao){
        int id = generaIdChat(dao);
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("Insert into chats (id,nombre,ultimoMensaje,chat) values (?, ?, ?, ? )");
            ps.setInt(1, id);
            String nombre = generaNombreChat(usuarios);
            ps.setString(2,nombre);
            ps.setString(3, Utils.pasaFechaString(LocalDateTime.now()));
            ps.setBoolean(4,true);
            ps.executeUpdate();
            for (User u : usuarios){
                ps = dao.getConexion().prepareStatement("Insert into chatUsuario (id,idUser) values (?, ?)");
                ps.setInt(1, id);
                ps.setInt(2, u.getId());
                ps.executeUpdate();
            }
            if (uTemp != null) {
                ps = dao.getConexion().prepareStatement("Insert into userAdmin (id,idUser) values (?, ?)");
                ps.setInt(1, id);
                ps.setInt(2, uTemp.getId());
                ps.executeUpdate();
            }
            dao.close();
            return new Chat(id,usuarios,uTemp,nombre,user,0,true);
        }catch (SQLException e) {
            return null;
        }
    }
    public static int generaIdChat(DaoManager dao){
        int id;
        do{
            id = (int) (Math.random() * 9999998 + 1);
        }while(estaChat(id,dao));
        return id;
    }
    public static boolean estaChat(int id,DaoManager dao){
        try {
            dao.open();
            Statement statement = dao.getConexion().createStatement();
            ResultSet resultados = statement.executeQuery("select * from chats where id = '" + id + "' order by ultimoMensaje desc limit 1");
            if (resultados.next()) {
                if (resultados.getInt("id") == id){
                    dao.close();
                    return true;
                }
                dao.close();
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean eliminaUserChat(User user,Chat chat,DaoManager dao){
        if (quitarUserAdminChat(user, chat,dao)) {
            try {
                dao.open();
                PreparedStatement ps = dao.getConexion().prepareStatement("delete from chatUsuario where idUser = " + user.getId() + " and id = " + chat.getId());
                ps.executeUpdate();
                dao.close();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
    public static boolean addUserAdminChat(User user,Chat chat,DaoManager dao){
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("Insert into userAdmin (id, idUser) values (?, ?)");
            ps.setInt(1,chat.getId());
            ps.setInt(2,user.getId());
            ps.executeUpdate();
            dao.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public static boolean addUserChat(User user,Chat chat,DaoManager dao){
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("Insert into chatUsuario (id, idUser) values (?, ?)");
            ps.setInt(1,chat.getId());
            ps.setInt(2,user.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public static boolean quitarUserAdminChat(User user,Chat chat,DaoManager dao){
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("Delete from userAdmin where id = " + chat.getId() + " and idUser = " + user.getId());
            ps.executeUpdate();
            dao.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean cambiarNombreChat(Chat chat, String nombre,DaoManager dao){
        try {
            dao.open();
            PreparedStatement ps = dao.getConexion().prepareStatement("update chats set nombre ='" + nombre + "' where id = " + chat.getId());
            ps.executeUpdate();
            dao.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
