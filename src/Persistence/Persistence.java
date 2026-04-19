package Persistence;

import Controller.GestionaApp;
import Models.User;

import java.io.*;

public class Persistence {
    public static boolean guardaUser(GestionaApp gestionaApp){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Data/Usuarios/" + gestionaApp.getUsuario().getId()));
            oos.writeObject(gestionaApp);
            oos = new ObjectOutputStream(new FileOutputStream("Data/UsuarioEnUso/" + gestionaApp.getUsuario().getId()));
            oos.writeObject(gestionaApp);
            oos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static GestionaApp CojeUser(){
        try {
            String id = obtenId();
            if (id != null) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Data/UsuarioEnUso/" + id));
                return (GestionaApp) ois.readObject();
            }
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e){
            return null;
        }
        return null;
    }
    public static void eliminaUsuariosEnUso(){
        File directorio = new File("Data/UsuarioEnUso");
        String [] ficheros = directorio.list();
        if (ficheros != null){
            for (String fichero: ficheros){
                File file = new File(directorio.getPath() + "/" + fichero);
                file.delete();
            }
        }
    }
    public static void eliminaUsuario(User user){
        File file = new File("Data/UsuarioEnUso/" + user.getId());
        file.delete();
        file = new File("Data/Usuarios/" + user.getId());
        file.delete();
    }
    public static String obtenId(){
        File directorio = new File("Data/UsuarioEnUso");
        String [] ficheros = directorio.list();
        if (ficheros != null && ficheros.length == 1) return ficheros[0];
        return null;
    }
}
