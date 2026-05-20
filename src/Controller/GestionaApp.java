package Controller;

import DAO.DaoChatSQL;
import DAO.DaoManager;
import DAO.DaoMensajeSQL;
import DAO.DaoUserSQL;
import Data.Data;
import Models.Chat;
import Models.User;
import Persistence.Persistence;

import java.io.Serializable;
import java.util.ArrayList;

public class GestionaApp implements Serializable {
    private User usuario;
    private ArrayList<Chat> chats;
    private int chatEnUso;
    private transient DaoManager dao;

    public GestionaApp() {
        dao = DaoManager.getSinglentonInstance();
        Persistence.existenCarpetas();
        GestionaApp gestionaApp = Persistence.CojeUser();
        if (gestionaApp != null){
            usuario = gestionaApp.getUsuario();
            chats = gestionaApp.getChats();
            chatEnUso = gestionaApp.chatEnUso;
        } else chats = new ArrayList<>();
    }
    public ArrayList<Chat> getChats() {
        return chats;
    }

    public User getUsuario() {
        return usuario;
    }

    public int getChatEnUso() {
        return chatEnUso;
    }

    public void setChatEnUso(int chatEnUso) {
        this.chatEnUso = chatEnUso;
    }

    public DaoManager getDao() {
        return dao;
    }

    public void setDao(DaoManager dao) {
        this.dao = dao;
    }

    //Otros metodos
    public void mock(){
        for (int i = 0; i < 1200; i++) {
            DaoUserSQL.crearUsuario("email" + (i + 1) + "@gmail.com","1234",dao);
        }
        User user = DaoUserSQL.buscaUsuarioEmail("email1000@gmail.com",dao);
        for (int i = 0; i < 10; i++) {
            ArrayList<User> users = new ArrayList<>();
            users.add(user);
            users.add(DaoUserSQL.buscaUsuarioEmail("email" + (i + 1) + "@gmail.com",dao));
            addChat(users);
        }
    }
    public boolean addUser(String email, String clave){
        if (buscaUserActivos(email) != null) return false;
        if (buscaUserBorrados(email) != null) borrarUserBorrados(email);
        else{ DaoUserSQL.crearUsuario(email,clave,dao);
            return true;
        }
        return false;
    }
    public User recuperarUser(String email,String clave){
        User user = DaoUserSQL.buscaUsuarioBorradoEmail(email,dao);
        if (user != null) {
            usuario = DaoUserSQL.recuperaUser(user,clave,dao);
            cargaChats();
            return usuario;
        }
        return null;
    }
    public User buscaUserActivos(String email){
        return DaoUserSQL.buscaUsuarioEmail(email,dao);
    }
    public User buscaUserBorrados(String email){
        return DaoUserSQL.buscaUsuarioBorradoEmail(email,dao);
    }
    public void borrarUserBorrados(String email){
        DaoUserSQL.borraUserBorrado(DaoUserSQL.buscaUsuarioBorradoEmail(email,dao),dao);
    }
    public User login(String email,String clave){
        usuario = DaoUserSQL.iniciarSesion(email,clave,dao);
        if (usuario != null) {
            cargaChats();
            guardaUser();
        }
        return usuario;
        }
    public void cargaChats(){
        chats = DaoChatSQL.cargaChats(usuario,dao);
    }
    public void cargaChat(int id){
        Chat chat = DaoChatSQL.cargaChat(usuario,id,dao);
        actualizaChat(chat);
    }
    public void actualizaChat(Chat chat){
        if (chats != null) {
            int longitud = chats.size();
            chats.remove(buscaChat(chat.getId()));
            if (chats.size() == longitud - 1) chats.addFirst(chat);
        }
    }
    public void addMensaje(Chat chat, int idUser,String texto){
        //chat.addMensaje(mensaje.getTexto(),mensaje.getUsuario(),chat.getId());
        DaoMensajeSQL.addMensaje(idUser,texto,chat,dao);
        chats.removeIf(c -> c.getId() == chat.getId());
        chats.addFirst(chat);
    }
    public void ponerMensajesLeidos(Chat chat){
        DaoMensajeSQL.ponerMensajesLeidos(usuario.getId(),chat.getId(),dao);
    }
    public void addMensajeBienvenidaGrupo(String emailCreador,int idChat){
        DaoMensajeSQL.addMensaje(0,Data.mensajeCreacion(emailCreador),buscaChat(idChat),dao);
    }


    public Chat getChat(int id) {
        for (Chat c : chats){
            if (c.getId() == id) return c;
        }
        return null;
    }
    public boolean addGrupo(ArrayList<User> users,String nombre,User user){
        Chat chat = DaoChatSQL.crearGrupo(users,usuario,nombre,user,dao);
        if (chat != null){
            if (!chats.isEmpty()) chats.addFirst(chat);
            else chats.add(chat);
            return true;
        }
        return false;
    }
    public boolean addChat(ArrayList<User> users){
        Chat chat = DaoChatSQL.crearChat(users,null,usuario,dao);
        if (chat != null){
            chats.addFirst(chat);
            return true;
        }
        return false;
    }
    public Chat buscaChat(int id){
        for (Chat c: chats){
            if (c.getId() == id) return c;
        }
        return null;
    }
    //Buscar en base de datos
    public Chat buscaChat(ArrayList<User> users){
        for (Chat c: chats){
            if (c.getUsuarios().size() == users.size()){
                if (estanUsuarios(users,c)) return c;
            }
        }
        return null;
    }
    public boolean estanUsuarios(ArrayList<User> users,Chat c){
        boolean estaUser;
        for (User user : c.getUsuarios()){
            estaUser = false;
            for (User u : users){
                if (user.getId() == u.getId()) estaUser = true;
            }
            if (!estaUser) return false;
        }
        return true;
    }
    public boolean borrarChat(Chat chat){
        return DaoMensajeSQL.eliminaMensajesChat(chat,usuario,dao);
    }
    public boolean eliminaUserChat(Chat chat,User user){
        if (DaoChatSQL.eliminaUserChat(user, chat,dao)){
            chat.borraUser(user.getEmail());
            return true;
        }
        return false;
    }
    public void borrarCuenta(User user){
        DaoUserSQL.actualizaFecha(user,dao);
        DaoUserSQL.borraUsuario(user,chats,dao);
        Persistence.eliminaUsuario(user);
    }
    public boolean addAdmin(Chat chat,User user){
        if (DaoChatSQL.addUserAdminChat(user,chat,dao)) {
            chat.addUserAdmin(user);
            return true;
        }
        return false;
    }
    public boolean quitaAdmin(Chat chat, User user){
        if (DaoChatSQL.quitarUserAdminChat(user, chat,dao)) {
            chat.quitarUserAdmin(user);
            return true;
        }
        return false;
    }
    public boolean addUserChat(Chat chat, User user){
        if (DaoChatSQL.addUserChat(user, chat,dao)){
            chat.addUser(user);
            return true;
        }
        return false;
    }
    public boolean cambiaNombreChat(Chat chat, String nombre){
        if (DaoChatSQL.cambiarNombreChat(chat, nombre,dao)){
            chat.setNombre(nombre);
            return true;
        }
        return false;
    }
    public boolean cambiaEmail(String email,User user){
        if (DaoUserSQL.cambiarEmailUsuario(user,email,dao)){
            user.setEmail(email);
            return true;
        }
        return false;
    }
    public boolean cambiaClave(String clave,User user){
        return DaoUserSQL.cambiarClaveUsuario(user, clave,dao);
    }
    public void eliminaUsuarioEnUso(){
        Persistence.eliminaUsuariosEnUso();
    }
    public boolean guardaUser(){
        return Persistence.guardaUser(this);
    }
    public void cambiaChatUso(int chat){
        chatEnUso = chat;
        guardaUser();
    }
    public boolean ultimoMensajeUser(){
        return buscaChat(chatEnUso).getMensajes().getLast().getUsuario().getId() == usuario.getId();
    }

    public boolean buscaCambios(){
        Chat chat;
        if (chatEnUso != -1) {
                chat = DaoChatSQL.cargaChat(usuario,chatEnUso,dao);
                if (chat != null) {
                            if(!chat.getUltimoMensaje().isEqual(buscaChat(chatEnUso).getUltimoMensaje())) return true;
                } else return false;
        }/* else {
                chats = DAO.cargaChats(usuario);
                if (chats != this.chats) return true;
        }*/
        return false;
    }
    public boolean esChat(Chat chat){
        return chat.isChat();
    }
    public ArrayList<Chat> getChatsConMensajes(){
        return DaoChatSQL.cargaChatsConMensajes(usuario,dao);
    }
    public boolean eliminaGrupo(Chat chat){
        return DaoChatSQL.eliminaGrupo(chat,usuario,dao);
    }
    public boolean eliminaChat(Chat chat){
        return DaoChatSQL.eliminaChat(chat,usuario,dao);
    }



}
