package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Chat {
    private int id;
    private ArrayList<Mensaje> mensajes;
    private ArrayList<User> usuarios;
    private LocalDateTime ultimoMensaje;
    private ArrayList<User> usersAdmins;

    public Chat(int id, ArrayList<User> usuarios, User uTemp) {
        this.id = id;
        this.usuarios = usuarios;
        ultimoMensaje = LocalDateTime.now();
        usersAdmins = new ArrayList<>();
        usersAdmins.add(uTemp);
        mensajes = new ArrayList<>();
    }

    public Chat(int id, ArrayList<Mensaje> mensajes, ArrayList<User> usuarios, ArrayList<User> usersAdmins) {
        this.id = id;
        this.usuarios = usuarios;
        this.usersAdmins = usersAdmins;
        this.mensajes = mensajes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<User> getUsuarios() {
        return usuarios;
    }
    public ArrayList<User> getUsuarios(String email){
        ArrayList<User> users = new ArrayList<>();
        for (User u: usuarios){
            if (!u.getEmail().equals(email)) users.add(u);
        }
        return users;
    }

    public ArrayList<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public void setUsuarios(ArrayList<User> usuarios) {
        this.usuarios = usuarios;
    }

    public LocalDateTime getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(LocalDateTime ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public ArrayList<User> getUsersAdmins() {
        return usersAdmins;
    }
    public ArrayList<User> getUsersAdmins(User uTemp) {
        ArrayList<User> admins = new ArrayList<>();
        for (User u : usersAdmins){
            if (!u.getEmail().equals(uTemp.getEmail())) admins.add(u);
        }
        return admins;
    }

    public void setUsersAdmins(ArrayList<User> usersAdmins) {
        this.usersAdmins = usersAdmins;
    }

    public User buscaUser(String email){
        for (User user : usuarios){
            if (user.getEmail().equals(email)) return user;
        }
        return null;
    }
    public void addMensaje(String mensaje,User usuario,int idChat){
        if (usuario.getEmail().equals("Bienvenido") || buscaUser(usuario.getEmail()) != null){
            ultimoMensaje = LocalDateTime.now();
            mensajes.add(new Mensaje(usuario,mensaje,idChat));
        }
    }


    public void addMensaje(Mensaje mensaje) {
        mensajes.add(mensaje);
    }



    public void borraUser(String email){
        usuarios.removeIf(user -> user.getEmail().equals(email));
    }
    public boolean comprobarUserAdmin(User uTemp){
        if (usersAdmins.isEmpty()) return false;
        for (User u : usersAdmins){
            if (u.getEmail().equals(uTemp.getEmail())) return true;
        }
        return false;
    }
    public ArrayList<User> getUsersNoAdmins(){
        ArrayList<User> usersNoAdmin = new ArrayList<>();
        for (User user : usuarios){
            if (!comprobarUserAdmin(user)) usersNoAdmin.add(user);
        }
        return usersNoAdmin;
    }
    public void addUserAdmin(User user){
        if (getUsersNoAdmins().contains(user)){
            usersAdmins.add(user);
        }
    }
    public void quitarUserAdmin(User user){
        if (getUsersAdmins().contains(user)){
            usersAdmins.remove(user);
        }
    }
    public void addUser(User user){
        usuarios.add(user);
    }

}
