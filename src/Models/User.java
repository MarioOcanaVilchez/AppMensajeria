package Models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private int id;
    private String email;

    private ArrayList<ArrayList<Mensaje>> mensajes;

    public User(String email) {
        this.email = email;
    }

    public User(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public User(int id) {
        this.id = id;
        email = null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
