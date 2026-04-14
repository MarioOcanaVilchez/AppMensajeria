package View;

import DAO.DAO;
import Models.User;

public class Pruebas {
    static void main() {
        User user = DAO.iniciarSesion("prueba@gmail.com","prueba");
        if (user != null) System.out.println(user.getEmail());
        if (DAO.crearUsuario("Prueba2@gmail.com","1234")) System.out.println("Usuario creado");
        else System.out.println("User no creado");
        user = DAO.buscaUsuarioEmail("Prueba2@gmail.com");
    }
}
