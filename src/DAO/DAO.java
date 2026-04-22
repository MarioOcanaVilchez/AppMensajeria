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
                    User user = new User(id,resultados.getString("email"));
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
                ResultSet resultados = statement.executeQuery("select * from usuariosActivos where id = " + user.getId() + " order by ultimaConexion desc limit 1");
                resultados.next();
                String clave = resultados.getString("clave");
                PreparedStatement ps = conexionBD.prepareStatement("delete from usuariosActivos where id = " + user.getId());
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
            id = (int) (Math.random() * 9999998 + 1);
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
                resultados.next();
                User user = new User(id,resultados.getString("email"));
                actualizaFecha(user);
                cierraConexion(conexionBD);
                return user;
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
        email = email.replace('\'',' ');
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
                PreparedStatement ps = conexionBD.prepareStatement("delete from usuariosBorrados where id = " + user.getId());
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
                    ArrayList<User> usuarios = new ArrayList<>();
                    ArrayList<User> admins = new ArrayList<>();
                    ArrayList<Mensaje> mensajes = new ArrayList<>();
                    String nombre;
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
                    statement = conexionBD.createStatement();
                    resultados = statement.executeQuery("select * from mensajeUsuario where idChat = " + id + " and idUserReceptor = " + user.getId() + " order by fechaEnviado asc");
                    //Cogemos los usuarios de los chats en los que esta
                    while(resultados.next()) {
                        if (buscaUsuarioId(resultados.getInt("idUserEnvia")) != null) mensajes.add(new Mensaje(buscaUsuarioId(resultados.getInt("idUserEnvia")),resultados.getString("texto"),id,Utils.pasarStringFecha(resultados.getString("fechaEnviado"))));
                        else if (buscaUsuarioBorradoId(resultados.getInt("idUserEnvia")) != null) mensajes.add(new Mensaje(buscaUsuarioBorradoId(resultados.getInt("idUserEnvia")),resultados.getString("texto"),id,Utils.pasarStringFecha(resultados.getString("fechaEnviado"))));
                        else mensajes.add(new Mensaje(new User(0,"Administración"),resultados.getString("texto"),id,Utils.pasarStringFecha(resultados.getString("fechaEnviado"))));
                    }
                    statement = conexionBD.createStatement();
                    resultados = statement.executeQuery("select * from chats where id = " + id + " order by ultimoMensaje desc limit 1");
                    //Cogemos los usuarios de los chats en los que esta
                    resultados.next();
                    nombre = resultados.getString("nombre");
                    chats.add(new Chat(id,mensajes,usuarios,admins,nombre,user,Utils.pasarStringFecha(resultados.getString("ultimoMensaje"))));
                }
                cierraConexion(conexionBD);
            }
        } catch (SQLException e) {
            return null;
        }
        return chats;
    }
    public static void addMensaje(Mensaje mensaje,Chat chat) {
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("Insert into mensajeChat (idChat,idUserEnvia,texto,fechaEnviado) values (?, ?, ?, ?)");
                ps.setInt(1, chat.getId());
                ps.setInt(2, mensaje.getUsuario().getId());
                ps.setString(3, mensaje.getTexto());
                ps.setString(4, Utils.pasaFechaString(LocalDateTime.now()));
                ps.executeUpdate();
                for (User u : chat.getUsuarios()) {
                    ps = conexionBD.prepareStatement("Insert into mensajeUsuario (idChat,idUserEnvia,idUserReceptor,texto,fechaEnviado) values (?, ?, ?, ?,?)");
                    ps.setInt(1, chat.getId());
                    ps.setInt(2, mensaje.getUsuario().getId());
                    ps.setInt(3,u.getId());
                    ps.setString(4, mensaje.getTexto());
                    ps.setString(5, Utils.pasaFechaString(LocalDateTime.now()));
                    ps.executeUpdate();
                }
                ps = conexionBD.prepareStatement("update chats set ultimoMensaje = '" + Utils.pasaFechaString(LocalDateTime.now()) + "' where id = " + chat.getId());
                ps.executeUpdate();
                cierraConexion(conexionBD);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static String generaNombreChat(ArrayList<User> users) {
        String nombre = "Chat de ";
        for (int i = 0; i < users.size(); i++) {
            if (i != users.size() - 1) nombre += users.get(i).getEmail() + ", ";
            else nombre += users.get(i).getEmail();
        }
        return nombre;
    }
    public static Chat crearChat(ArrayList<User>usuarios,User uTemp,String nombre,User user){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            int id = generaIdChat();
            try {
                PreparedStatement ps = conexionBD.prepareStatement("Insert into chats (id,nombre,ultimoMensaje) values (?, ?, ?)");
                ps.setInt(1, id);
                if (nombre == null) nombre = generaNombreChat(usuarios);
                ps.setString(2,nombre);
                ps.setString(3, Utils.pasaFechaString(LocalDateTime.now()));
                ps.executeUpdate();
                for (User u : usuarios){
                    ps = conexionBD.prepareStatement("Insert into chatUsuario (id,idUser) values (?, ?)");
                    ps.setInt(1, id);
                    ps.setInt(2, u.getId());
                    ps.executeUpdate();
                }
                if (uTemp != null) {
                    ps = conexionBD.prepareStatement("Insert into userAdmin (id,idUser) values (?, ?)");
                    ps.setInt(1, id);
                    ps.setInt(2, uTemp.getId());
                    ps.executeUpdate();
                }
                cierraConexion(conexionBD);
                return new Chat(id,usuarios,uTemp,nombre,user);
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    public static int generaIdChat(){
        int id;
        do{
            id = (int) (Math.random() * 9999998 + 1);
        }while(estaChat(id));
        return id;
    }
    public static boolean estaChat(int id){
        try {
            Connection conexionBD = iniciarConexion();
            if(conexionBD != null) {
                Statement statement = conexionBD.createStatement();
                ResultSet resultados = statement.executeQuery("select * from chats where id = '" + id + "' order by ultimoMensaje desc limit 1");
                if (resultados.next()) {
                    if (resultados.getInt("id") == id) return true;
                }
                cierraConexion(conexionBD);
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
    public static boolean eliminaUserChat(User user,Chat chat){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            if (quitarUserAdminChat(user, chat)) {
                try {
                    PreparedStatement ps = conexionBD.prepareStatement("delete from chatUsuario where idUser = " + user.getId() + " and id = " + chat.getId());
                    ps.executeUpdate();
                    cierraConexion(conexionBD);
                    return true;
                } catch (SQLException e) {
                    return false;
                }
            }
            return false;
        }
        return false;
    }
    public static boolean addUserAdminChat(User user,Chat chat){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("Insert into userAdmin (id, idUser) values (?, ?)");
                ps.setInt(1,chat.getId());
                ps.setInt(2,user.getId());
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
    public static boolean addUserChat(User user,Chat chat){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("Insert into chatUsuario (id, idUser) values (?, ?)");
                ps.setInt(1,chat.getId());
                ps.setInt(2,user.getId());
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
    public static boolean quitarUserAdminChat(User user,Chat chat){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("Delete from userAdmin where id = " + chat.getId() + " and idUser = " + user.getId());
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
    public static boolean cambiarNombreChat(Chat chat, String nombre){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("update chats set nombre ='" + nombre + "' where id = " + chat.getId());
                ps.executeUpdate();
                cierraConexion(conexionBD);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
    public static boolean cambiarEmailUsuario(User user, String nombre){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("update usuariosActivos set email ='" + nombre + "' where id = " + user.getId());
                ps.executeUpdate();
                cierraConexion(conexionBD);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }
    public static boolean cambiarClaveUsuario(User user, String clave){
        Connection conexionBD = iniciarConexion();
        if (conexionBD != null) {
            try {
                PreparedStatement ps = conexionBD.prepareStatement("update usuariosActivos set clave ='" + clave + "' where id = " + user.getId());
                ps.executeUpdate();
                cierraConexion(conexionBD);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

}
