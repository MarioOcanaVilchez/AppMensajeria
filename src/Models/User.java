package Models;

import java.util.ArrayList;

public class User {
    private int id;
    private String email;

    private ArrayList<ArrayList<Mensaje>> mensajes;

    public User(String email, String clave) {
        this.email = email;
    }

    public User(int id, String email) {
        this.id = id;
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public ArrayList<ArrayList<Mensaje>> getMensajes() {
        return mensajes;
    }

    public void setMensajes(ArrayList<ArrayList<Mensaje>> mensajes) {
        this.mensajes = mensajes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
