package Controller;

import DAO.DAO;
import Data.Data;
import Models.Chat;
import Models.User;
import Models.Mensaje;
import Utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class GestionaApp {
    private ArrayList<User> usuariosActivos;
    private ArrayList<Chat> chats;
    private ArrayList<User> usuariosBorrados;

    public GestionaApp() {
        usuariosActivos = new ArrayList<>();
        chats = new ArrayList<>();
        usuariosBorrados = new ArrayList<>();
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    //Otros metodos
    public void mock(){
        for (int i = 0; i < 50000; i++) {
            usuariosActivos.add(new User(i,"email" + (i + 1) + "@gmail.com","1234",Utils.generaFechaAleatoria()));
        }
        ordenaUsersActivos();
        for (int i = 0; i < 2385; i++) {
            usuariosBorrados.add(new User(i + 50000,"email" + (i + 50000) + "@gmail.com","1234",Utils.generaFechaAleatoria()));
        }
        ordenaUsersBorrados();
        for (int i = 0; i < 10; i++) {
            ArrayList<User> users = new ArrayList<>();
            users.add(buscaUserActivos("email1000@gmail.com"));
            users.add(buscaUserActivos("email" + (i + 1) + "@gmail.com"));
            addChat(users,null);
        }
    }
    public boolean addUser(String email, String clave){
        if (buscaUserActivos(email) != null) return false;
        if (buscaUserBorrados(email) != null) borrarUserBorrados(email);
        if (DAO.buscaUsuarioEmail(email) == null){
            DAO.crearUsuario(email,clave);
            usuariosActivos.addFirst(DAO.buscaUsuarioEmail(email));
            return true;
        }
        return false;
    }
    public User recuperarUser(String email,String clave){
        User user = DAO.buscaUsuarioBorradoEmail(email, clave);
        if (user != null) {
            DAO.recuperaUser(user);
            usuariosActivos.add(user);
            return user;
        }
        return null;
    }
    public User buscaUserActivos(String email){
        for (User user : usuariosActivos){
            if (user.getEmail().equals(email)) return user;
        }
        User user = DAO.buscaUsuarioEmail(email);
        if (user != null){
            usuariosActivos.add(user);
            return user;
        }
        return null;
    }
    public User buscaUserBorrados(String email){
        for (User user : usuariosBorrados){
            if (user.getEmail().equals(email)) return user;
        }
        return null;
    }
    public void borrarUserBorrados(String email){
        usuariosBorrados.removeIf(user -> user.getEmail().equals(email));
    }
    public User login(String email,String clave){
        return DAO.iniciarSesion(email,clave);
        }
    //selection sort es mas rápido
    public  void ordenaUsersActivos(){
        int posMasGrande = 0;
        LocalDateTime fechaMasGrande = null;
        User uAux;
        for (int i = 0; i <usuariosActivos.size() ; i++) {
            for (int j = i; j < usuariosActivos.size(); j++) {
                if (j == i){
                    posMasGrande = i;
                    fechaMasGrande = usuariosActivos.get(i).getUltimaConexion();
                } else {
                    if (usuariosActivos.get(j).getUltimaConexion().isAfter(fechaMasGrande)){
                        fechaMasGrande = usuariosActivos.get(j).getUltimaConexion();
                        posMasGrande = j;
                    }
                }
            }
            uAux = usuariosActivos.get(i);
            usuariosActivos.set(i,usuariosActivos.get(posMasGrande));
            usuariosActivos.set(posMasGrande,uAux);
        }
    }
    public void ordenaUsersBorrados(){
        int posMasGrande = 0;
        LocalDateTime fechaMasGrande = null;
        User uAux;
        for (int i = 0; i <usuariosBorrados.size() ; i++) {
            for (int j = i; j < usuariosBorrados.size(); j++) {
                if (j == i){
                    posMasGrande = i;
                    fechaMasGrande = usuariosBorrados.get(i).getUltimaConexion();
                } else {
                    if (usuariosBorrados.get(j).getUltimaConexion().isAfter(fechaMasGrande)){
                        fechaMasGrande = usuariosBorrados.get(j).getUltimaConexion();
                        posMasGrande = j;
                    }
                }
            }
            uAux = usuariosBorrados.get(i);
            usuariosBorrados.set(i,usuariosBorrados.get(posMasGrande));
            usuariosBorrados.set(posMasGrande,uAux);
        }
    }
    public ArrayList<Chat> buscaChats(User user){
        ArrayList<Chat> chatUser = new ArrayList<>();
        for (Chat c : chats){
            if (c.buscaUser(user.getEmail()) != null) chatUser.add(c);
        }
        return chatUser;
    }
    public void addMensaje(Chat chat, Mensaje mensaje){
        chat.addMensaje(mensaje.getTexto(),mensaje.getUsuario(),chat.getId());
        chats.removeIf(c -> c.getId() == chat.getId());
        chats.addFirst(chat);
    }
    public void addMensajeBienvenidaGrupo(String emailCreador,int idChat){
        chats.getFirst().addMensaje(Data.mensajeCreacion(emailCreador),new User("Bienvenido",null),idChat);
    }
    public void addChat(ArrayList<User> users, User uTemp){
        chats.addFirst(new Chat(generaIdChat(),users, uTemp));
        for (int i = 0; i < users.size(); i++) {
            users.get(i).addPrimerMensaje(chats.getFirst().getId());
        }
    }
    public int generaIdChat(){
        int id;
        do{
            id = (int) (Math.random() * 100000);
        }while(buscaChat(id) != null);
        return id;
    }
    public Chat buscaChat(int id){
        for (Chat c: chats){
            if (c.getId() == id) return c;
        }
        return null;
    }
    public Chat buscaChat(ArrayList<User> users){
        for (Chat c: chats){
            if (c.getUsuarios().containsAll(users) && c.getUsuarios().size() == users.size()) return c;
        }
        return null;
    }
    public void borrarChat(Chat chat,User uTemp){
        uTemp.borraChat(chat.getId());
        if (chat.comprobarUserAdmin(uTemp)) chat.quitarUserAdmin(uTemp);
        if (chat.getUsuarios().size() == 2) chats.remove(chat);
        else{
            chat.borraUser(uTemp.getEmail());
            ArrayList<User> users = chat.getUsuarios();
            chats.remove(chat);
            if (buscaChat(users) == null){
                chats.add(chat);
            }
        }
    }
    //todo bug al recuperar cuenta y eliminarla
    public void borrarCuenta(User user){
        for (Chat c: buscaChats(user)){
            borrarChat(c,user);
        }
        usuariosActivos.remove(user);
        user.setUltimaConexion(LocalDateTime.now());
        DAO.borraUsuario(user);
        usuariosBorrados.addFirst(DAO.buscaUsuarioBorradoEmail(user.getEmail(),user.getClave()));
    }
    public void ordenaChats(){
        ArrayList<Chat> chatsOrdenados = new ArrayList<>();
        Chat chatMasAntiguo = null;
        int veces = chats.size();
        for (int i = 0; i < veces; i++) {
            for (int j = 0; j < chats.size(); j++) {
                if (chatMasAntiguo == null) chatMasAntiguo = chats.get(j);
                else if (chatMasAntiguo.getUltimoMensaje().isAfter(chats.get(j).getUltimoMensaje())) chatMasAntiguo = chats.get(j);
            }
            chatsOrdenados.addFirst(chatMasAntiguo);
            for (Chat c : chats){
                if (c.getId() == chatMasAntiguo.getId()) chatsOrdenados.remove(c);
            }
            chatMasAntiguo = null;
        }
        chats = chatsOrdenados;
    }
}
