package Models;


import java.io.Serializable;
import java.time.LocalDateTime;

public class Mensaje implements Serializable {
    private long id;
    private User usuario;
    private String texto;
    private int idChat;
    private LocalDateTime fecha;

    public Mensaje(long id,User usuario, String texto, int idChat,LocalDateTime fecha) {
        this.usuario = usuario;
        this.id = id;
        this.texto = texto;
        this.idChat = idChat;
        this.fecha = fecha;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getIdChat() {
        return idChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
