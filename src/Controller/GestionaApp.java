package Controller;

import DAO.DAO;
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
    public GestionaApp() {
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

    //Otros metodos
    public void mock(){
        for (int i = 0; i < 50000; i++) {
            DAO.crearUsuario("email" + (i + 1) + "@gmail.com","1234");
        }
        User user = DAO.buscaUsuarioEmail("email1000@gmail.com");
        for (int i = 0; i < 10; i++) {
            ArrayList<User> users = new ArrayList<>();
            users.add(user);
            users.add(DAO.buscaUsuarioEmail("email" + (i + 1) + "@gmail.com"));
            addChat(users,null,null,user);
        }
    }
    public boolean addUser(String email, String clave){
        if (buscaUserActivos(email) != null) return false;
        if (buscaUserBorrados(email) != null) borrarUserBorrados(email);
        else{ DAO.crearUsuario(email,clave);
            return true;
        }
        return false;
    }
    public User recuperarUser(String email,String clave){
        User user = DAO.buscaUsuarioBorradoEmail(email);
        if (user != null) {
            return DAO.recuperaUser(user,clave);
        }
        return null;
    }

    public User buscaUserActivos(String email){
        return DAO.buscaUsuarioEmail(email);
    }
    public User buscaUserBorrados(String email){
        return DAO.buscaUsuarioBorradoEmail(email);
    }
    public void borrarUserBorrados(String email){
        DAO.borraUserBorrado(DAO.buscaUsuarioBorradoEmail(email));
    }
    public User login(String email,String clave){
        usuario = DAO.iniciarSesion(email,clave);
        if (usuario != null) {
            cargaChats();
            Persistence.guardaUser(this);
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
         chats = DAO.cargaChats(usuario);
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
        DAO.addMensaje(mensaje,chat);
        chats.removeIf(c -> c.getId() == chat.getId());
        chats.addFirst(chat);
    }
    public void addMensajeBienvenidaGrupo(String emailCreador,int idChat){
        DAO.addMensaje(new Mensaje(new User(1,"Bievenido"),Data.mensajeCreacion(emailCreador),idChat,LocalDateTime.now()),buscaChat(idChat));
        chats.getFirst().addMensaje(Data.mensajeCreacion(emailCreador),new User(0,"Bienvenido"),idChat);
    }


    public Chat getChat(int id) {
        for (Chat c : chats){
            if (c.getId() == id) return c;
        }
        return null;
    }
    public boolean addChat(ArrayList<User> users,User uTemp,String nombre,User user){
        Chat chat = DAO.crearChat(users,uTemp,nombre,user);
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
            if (c.getUsuarios().containsAll(users) && c.getUsuarios().size() == users.size()) return c;
        }
        return null;
    }
    public boolean borrarChat(Chat chat,User uTemp){
        if (DAO.eliminaUserChat(uTemp,chat)) {
            chats.remove(getChat(chat.getId()));
            if (chat.comprobarUserAdmin(uTemp)) chat.quitarUserAdmin(uTemp);
            if (chat.getUsuarios().size() == 2) chats.remove(chat);
            else {
                chat.borraUser(uTemp.getEmail());
                ArrayList<User> users = chat.getUsuarios();
                chats.remove(chat);
                if (buscaChat(users) == null) {
                    chats.add(chat);
                }
            }
            return true;
        }
        return false;
    }
    public boolean eliminaUserChat(Chat chat,User user){
        if (DAO.eliminaUserChat(user, chat)){
            chat.borraUser(user.getEmail());
            return true;
        }
        return false;
    }
    public void borrarCuenta(User user){
        for (Chat c: buscaChats(user)){
            borrarChat(c,user);
        }
        DAO.actualizaFecha(user);
        DAO.borraUsuario(user);
        Persistence.eliminaUsuario(user);
    }
    public boolean addAdmin(Chat chat,User user){
        if (DAO.addUserAdminChat(user,chat)) {
            chat.addUserAdmin(user);
            return true;
        }
        return false;
    }
    public boolean quitaAdmin(Chat chat, User user){
        if (DAO.quitarUserAdminChat(user, chat)) {
            chat.quitarUserAdmin(user);
            return true;
        }
        return false;
    }
    public boolean addUserChat(Chat chat, User user){
        if (DAO.addUserChat(user, chat)){
            chat.addUser(user);
            return true;
        }
        return false;
    }
    public boolean cambiaNombreChat(Chat chat, String nombre){
        if (DAO.cambiarNombreChat(chat, nombre)){
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
        if (DAO.cambiarEmailUsuario(user,email)){
            user.setEmail(email);
            return true;
        }
        return false;
    }
    public boolean cambiaClave(String clave,User user){
        return DAO.cambiarClaveUsuario(user, clave);
    }
    public void eliminaUsuarioEnUso(){
        Persistence.eliminaUsuariosEnUso();
    }
    public boolean guardaUser(){
        return Persistence.guardaUser(this);
    }
    public void cambiaChatUso(int chat){
        chatEnUso = chat;
        Persistence.guardaUser(this);
    }
    public boolean ultimoMensajeUser(){
        return buscaChat(chatEnUso).getMensajes().getLast().getUsuario().getId() == usuario.getId();
    }

    public boolean buscaCambios(){
        ArrayList<Chat> chats;
        if (chatEnUso != -1) {
                chats = DAO.cargaChats(usuario);
                if (chats != null) {
                    for (Chat c : chats) {
                        if (c.getId() == chatEnUso){
                            if(!c.getUltimoMensaje().isEqual(buscaChat(chatEnUso).getUltimoMensaje())){
                                return true;
                        }

                        }
                    }
                } else return false;
        } else {
                chats = DAO.cargaChats(usuario);
                if (chats != this.chats) return true;
        }
        return false;
    }



}
