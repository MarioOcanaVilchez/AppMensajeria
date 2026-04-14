package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class User {
    private String email;
    private LocalDateTime ultimaConexion;
    private String clave;
    private ArrayList<ArrayList<Mensaje>> mensajes;

    public User(String email, String clave) {
        this.email = email;
        this.clave = clave;
        ultimaConexion = LocalDateTime.now();
    }

    public User(String email, String clave, LocalDateTime ultimaConexion, LocalDateTime fechaBorrado) {
        this.email = email;
        this.clave = clave;
        this.ultimaConexion = ultimaConexion;
        mensajes = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public LocalDateTime getUltimaConexion() {
        return ultimaConexion;
    }

    public void setUltimaConexion(LocalDateTime ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public ArrayList<ArrayList<Mensaje>> getMensajes() {
        return mensajes;
    }

    public void setMensajes(ArrayList<ArrayList<Mensaje>> mensajes) {
        this.mensajes = mensajes;
    }

    public void addMensaje(Mensaje mensaje) {
        for (int i = 0; i < mensajes.size(); i++) {
            if (mensajes.get(i).getFirst().getIdChat() == mensaje.getIdChat()) {
                mensajes.get(i).add(mensaje);
            }
        }
    }

    public ArrayList<Mensaje> getChat(int id) {
        for (int i = 0; i < mensajes.size(); i++) {
            if (mensajes.get(i).getFirst().getIdChat() == id) {
                return mensajes.get(i);
            }
        }
        return null;
    }

    public void addPrimerMensaje(int id) {
        mensajes.add(new ArrayList<>());
        mensajes.getLast().add(new Mensaje(new User("", null), "", id));
    }

    public void borraChat(int id) {
        ArrayList<Mensaje> chat = getChat(id);
        mensajes.remove(chat);
    }
}
