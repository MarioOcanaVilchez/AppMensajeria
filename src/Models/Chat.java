package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Chat {
    private int id;
    private ArrayList<Mensaje> mensajes;
    private ArrayList<User> usuarios;
    private LocalDateTime ultimoMensaje;
    private ArrayList<User> usersAdmins;
    private String nombre;

    public Chat(int id, ArrayList<User> usuarios, User user,String nombre,User uTemp) {
        this.id = id;
        this.usuarios = usuarios;
        ultimoMensaje = LocalDateTime.now();
        usersAdmins = new ArrayList<>();
        usersAdmins.add(user);
        mensajes = new ArrayList<>();
        this.nombre = nombre;
        ajustaNombre(uTemp);
    }

    public Chat(int id, ArrayList<Mensaje> mensajes, ArrayList<User> usuarios, ArrayList<User> usersAdmins,String nombre,User uTemp) {
        this.id = id;
        this.usuarios = usuarios;
        this.usersAdmins = usersAdmins;
        this.mensajes = mensajes;
        this.nombre = nombre;
        ajustaNombre(uTemp);
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
    public void ajustaNombre(User uTemp){
        if (nombre.startsWith("Chat de ") && nombre.contains(uTemp.getEmail())) {
            int inicio = nombre.indexOf(uTemp.getEmail());
            int longitud = uTemp.getEmail().length();
            if (nombre.length() > inicio + longitud + 2 && inicio == 8)
                nombre = nombre.substring(0, inicio) + nombre.substring(inicio + longitud + 2);
            else if (nombre.length() <= inicio + longitud + 2) nombre = nombre.substring(0, inicio - 2);
            else nombre = nombre.substring(0, inicio - 2) + nombre.substring(inicio + longitud + 2);
            nombre = nombre.replace("Chat de ", "Chat con ");
        }
    }


}
