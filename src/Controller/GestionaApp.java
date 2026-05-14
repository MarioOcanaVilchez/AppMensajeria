package Controller;

import DAO.DaoChatSQL;
import DAO.DaoManager;
import DAO.DaoMensajeSQL;
import DAO.DaoUserSQL;
import Data.Data;
import Models.Chat;
import Models.User;
import Models.Mensaje;
import Persistence.Persistence;

import java.io.Serializable;
import java.time.LocalDateTime;
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
        for (int i = 0; i < 50000; i++) {
            DaoUserSQL.crearUsuario("email" + (i + 1) + "@gmail.com","1234",dao);
        }
        User user = DaoUserSQL.buscaUsuarioEmail("email1000@gmail.com",dao);
        for (int i = 0; i < 10; i++) {
            ArrayList<User> users = new ArrayList<>();
            users.add(user);
            users.add(DaoUserSQL.buscaUsuarioEmail("email" + (i + 1) + "@gmail.com",dao));
            addChat(users,null,null,user);
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
            return DaoUserSQL.recuperaUser(user,clave,dao);
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
    //selection sort es mas rápido
    /*public  void ordenaUsersActivos(){
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
    }*/
    /*public void ordenaUsersBorrados(){
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
    }*/
    public void cargaChats(){
        chats = DaoChatSQL.cargaChats(usuario,dao);
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
        DaoMensajeSQL.addMensaje(mensaje,chat,dao);
        chats.removeIf(c -> c.getId() == chat.getId());
        chats.addFirst(chat);
    }
    public void addMensajeBienvenidaGrupo(String emailCreador,int idChat){
        DaoMensajeSQL.addMensaje(new Mensaje(new User(0,"Bievenido"),Data.mensajeCreacion(emailCreador),idChat,LocalDateTime.now()),buscaChat(idChat),dao);
        chats.getFirst().addMensaje(Data.mensajeCreacion(emailCreador),new User(0,"Bienvenido"),idChat);
    }


    public Chat getChat(int id) {
        for (Chat c : chats){
            if (c.getId() == id) return c;
        }
        return null;
    }
    public boolean addChat(ArrayList<User> users,User uTemp,String nombre,User user){
        Chat chat = DaoChatSQL.crearChat(users,uTemp,nombre,user,dao);
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
    //todo add atributo boolean chat para distingir chat de grupo para expulsar usuario
    public boolean borrarChat(Chat chat,User uTemp){
        if (DaoChatSQL.eliminaUserChat(uTemp,chat,dao)) {
            chats.remove(getChat(chat.getId()));
            return true;
        }
        return false;
    }
    public boolean eliminaUserChat(Chat chat,User user){
        if (DaoChatSQL.eliminaUserChat(user, chat,dao)){
            chat.borraUser(user.getEmail());
            return true;
        }
        return false;
    }
    public void borrarCuenta(User user){
        for (Chat c: buscaChats(user)){
            borrarChat(c,user);
        }
        DaoUserSQL.actualizaFecha(user,dao);
        DaoUserSQL.borraUsuario(user,dao);
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
        Chat chat = null;
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



}
