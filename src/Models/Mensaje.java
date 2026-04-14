package Models;

import java.time.LocalDateTime;

public class Mensaje {
    private User usuario;
    private String texto;
    private int idChat;
    private LocalDateTime fechaEnviado;

    public Mensaje(User usuario, String texto, int idChat) {
        this.usuario = usuario;
        this.texto = texto;
        this.idChat = idChat;
        fechaEnviado = LocalDateTime.now();
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

    public LocalDateTime getFechaEnviado() {
        return fechaEnviado;
    }

    public void setFechaEnviado(LocalDateTime fechaEnviado) {
        this.fechaEnviado = fechaEnviado;
    }
}
