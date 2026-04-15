package View;

import Controller.GestionaApp;
import Models.Chat;
import Models.Mensaje;
import Models.User;
import Utils.Utils;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    static void main() {
        runApp();
    }

    public static Scanner scanner = new Scanner(System.in);

    public static void runApp() {
        GestionaApp gestionaApp = new GestionaApp();
        gestionaApp.mock();
        String op;
        User uTemp;
        do {
            uTemp = menuInicio(gestionaApp);
            if (uTemp != null) {
                Utils.limpiaPantalla();
                do {
                    op = pintaMenuPrincipal(uTemp);
                    switch (op) {
                        case "1":
                            verChats(gestionaApp, uTemp);
                            break;
                        case "2":
                            crearGrupo(gestionaApp, uTemp);
                            break;
                        case "3":
                            crearChat(gestionaApp, uTemp);
                            break;
                        case "4":
                            cambiarDatos(uTemp, gestionaApp);
                            break;
                        case "5":
                            borrarChat(gestionaApp, uTemp);
                            break;
                        case "6":
                            borrarCuenta(gestionaApp, uTemp);
                            break;
                        case "7":
                            break;
                        default:
                            System.out.println("Opción no existente");
                    }
                } while (!op.equals("7") && !op.equals("6"));
            }
        } while (uTemp != null);
    }

    //Gestion del menu de inicio
    public static User menuInicio(GestionaApp gestionaApp) {
        do {
            String op = pintaMenu();
            String email, clave;
            User usuario;
            switch (op) {
                case "1":
                    email = preguntaPers("Introduce tu email");
                    clave = preguntaPers("Introduce tu contraseña");
                    usuario = gestionaApp.login(email, clave);
                    if (usuario != null) return usuario;
                    else System.out.println("Usuario o contraseña incorrectos");
                    break;
                case "2":
                    email = preguntaPers("Introduce tu email");
                    clave = preguntaPers("Introduce tu contraseña");
                    if (gestionaApp.addUser(email, clave)) System.out.println("Usuario registrado");
                    else System.out.println("Usuario ya existente");
                    break;
                case "3":
                    email = preguntaPers("Introduce tu email");
                    clave = preguntaPers("Introduce tu contraseña");
                    usuario = gestionaApp.recuperarUser(email,clave);
                    if (usuario == null) System.out.println("No se ha encontrado ningún usuario");
                    else{
                        System.out.println("Usuario recuperado");
                        Utils.pulsaParaContinuar();
                        return usuario;
                    }
                    break;
                case "4":
                    return null;
                default:
                    System.out.println("Opción no existente");
                    break;
            }
            Utils.pulsaParaContinuar();
            Utils.limpiaPantalla();
        } while (true);
    }
    //Pintar el menu de inicio
    public static String pintaMenu() {
        System.out.println("""
                1. Inicia sesión
                2. Registrarse
                3. Recuperar cuenta
                4. Salir""");
        return preguntaPers("Introduce una opción");
    }

    //Preguntas que devuelven un String
    public static String preguntaPers(String mensaje) {
        System.out.print(mensaje + ": ");
        return scanner.nextLine();
    }

    //Pintar el menu principal
    public static String pintaMenuPrincipal(User uTemp) {
        System.out.println("User: " + uTemp.getEmail() + "\n");
        System.out.println("""
                1. Abrir chats
                2. Crear grupo
                3. Buscar usuario
                4. Cambiar datos
                5. Borrar chat
                6. Borrar cuenta
                7. Salir""");
        return preguntaPers("Introduce una opción");
    }

    //Pintar el menu para cambiar datos
    public static String menuCambiarDatos() {
        System.out.println("""
                1. Cambiar email
                2. Cambiar contraseña
                3. Salir""");
        return preguntaPers("Introduce una opción");
    }

    //Pintar los chats y grupos de un usuario
    public static void pintaChats(ArrayList<Chat> chats, User uTemp) {
        if (chats.isEmpty()) System.out.println("No hay chats");
        for (int i = 0; i < chats.size(); i++) {
            System.out.print((i + 1) + ". Chat con ");
            pintaUsersChat(chats.get(i), uTemp);
        }
        System.out.println((chats.size() + 1) + ". Salir");
    }

    //Pinta los usuarios del chat para identificarlo
    public static void pintaUsersChat(Chat chat, User uTemp) {
        ArrayList<User> users = chat.getUsuarios(uTemp.getEmail());
        for (int i = 0; i < users.size(); i++) {
            if (i != users.size() - 1) System.out.print(users.get(i).getEmail() + ", ");
            else System.out.println(users.get(i).getEmail());
        }
    }

    //Gestión de cambiar datos
    public static void cambiarDatos(User uTemp, GestionaApp gestionaApp) {
        String op;
        do {
            op = menuCambiarDatos();
            switch (op) {
                case "1":
                    cambiaEmail(uTemp, gestionaApp);
                    break;
                case "2":
                    cambiaClave(uTemp, gestionaApp);
                    break;
                case "3":
                    break;
                default:
                    System.out.println("Opción no existente");
                    break;
            }
            if (!op.equals("3")) {
                Utils.pulsaParaContinuar();
                Utils.limpiaPantalla();
            }
        } while (!op.equals("3"));
    }

    //Cambiar el email
    public static void cambiaEmail(User uTemp, GestionaApp gestionaApp) {
        String emailNuevo = preguntaPers("Introduce tu nuevo email");
        if (emailNuevo.equals(uTemp.getEmail())) System.out.println("Tu nuevo email no puede ser igual al anterior");
        else if (gestionaApp.buscaUserActivos(emailNuevo) != null)
            System.out.println("Email ya en uso por otro usuario");
        else {
            uTemp.setEmail(emailNuevo);
            System.out.println("Email actualizado");
        }
    }

    //Cambiar la clave
    public static void cambiaClave(User uTemp, GestionaApp gestionaApp) {
    /*    String claveNueva = preguntaPers("Introduce tu nueva contraseña");
        if (claveNueva.equals(uTemp.getClave()))
            System.out.println("Tu nueva contraseña no puede ser igual al anterior");
        else {
            uTemp.setClave(claveNueva);
            System.out.println("contraseña actualizado");
        }*/
    }

    //Menu para seleccionar un chat o grupo
    public static Chat seleccionaChat(GestionaApp gestionaApp, User uTemp) {
        int op;
        do {
            pintaChats(gestionaApp.getChats(), uTemp);
            try {
                op = Integer.parseInt(preguntaPers("Introduce una opción"));
            } catch (NumberFormatException e) {
                System.out.println("Opción no existente");
                op = Integer.MIN_VALUE;
            }
            if (op <= gestionaApp.getChats().size()) {
                if (op <= 0) System.out.println("Opción no existente");
                else {
                    Utils.limpiaPantalla();
                    return gestionaApp.getChats().get(op - 1);
                }
            }
        } while (op != gestionaApp.getChats().size() + 1);
        return null;
    }

    //ver los chats y usarlos
    public static void verChats(GestionaApp gestionaApp, User uTemp) {
        Chat chat;
        do {
            chat = seleccionaChat(gestionaApp, uTemp);
            if (chat != null) usaChat(chat, gestionaApp, uTemp);
        } while (chat != null);
    }

    //Usar un chat o grupo
    public static void usaChat(Chat chat, GestionaApp gestionaApp, User uTemp) {
        pintaChat(chat,uTemp,gestionaApp);
        String mensaje;
        do {
            mensaje = preguntaPers("mensaje o salir o admin");
            if (mensaje.equalsIgnoreCase("salir")){
                System.out.println("Volviendo al menú");
            } else if (mensaje.equalsIgnoreCase("admin")){
                menuAdmin(chat,uTemp,gestionaApp);
                Utils.limpiaPantalla();
                pintaChat(chat,uTemp,gestionaApp);
            } else {
                gestionaApp.addMensaje(chat, new Mensaje(uTemp, mensaje, chat.getId()));
                Utils.limpiaPantalla();
                pintaChat(chat,uTemp,gestionaApp);
            }
        } while (!mensaje.equalsIgnoreCase("salir"));
    }

    //Pintar un chat
    public static void pintaChat(Chat chat,User uTemp,GestionaApp gestionaApp) {
        /*for (int i = 0; i < chat.getMensajes().size(); i++) {
            if (i == 0) {
                System.out.println(chat.getMensajes().get(i).getUsuario().getEmail());
                System.out.println(chat.getMensajes().get(i).getTexto());

            } else {
                if (chat.getMensajes().get(i).getUsuario().equals(chat.getMensajes().get(i - 1).getUsuario()))
                    System.out.println(chat.getMensajes().get(i).getTexto());
                else {
                    System.out.println(chat.getMensajes().get(i).getUsuario().getEmail());
                    System.out.println(chat.getMensajes().get(i).getTexto());
                }
            }
        }*/
        if (chat != null) {
            for (int i = 0; i < chat.getMensajes().size(); i++) {
                if (i == 0) {
                    System.out.println(chat.getMensajes().get(i).getUsuario().getEmail());
                    System.out.println(chat.getMensajes().get(i).getTexto());

                } else {
                    if (chat.getMensajes().get(i).getUsuario().getEmail().equals(chat.getMensajes().get(i - 1).getUsuario().getEmail()))
                        System.out.println(chat.getMensajes().get(i).getTexto());
                    else {
                        System.out.println(chat.getMensajes().get(i).getUsuario().getEmail());
                        System.out.println(chat.getMensajes().get(i).getTexto());
                    }
                }
            }
        }
    }

    //Crear un grupo
    public static void crearGrupo(GestionaApp gestionaApp, User uTemp) {
        ArrayList<User> users = new ArrayList<>();
        String email;
        int cont = 1;
        do {
            email = preguntaPers("Introduce al miembro " + cont + " del chat o crear para terminar");
            if (!email.equalsIgnoreCase("crear")) {
                if (gestionaApp.buscaUserActivos(email) == null) System.out.println("Usuario no existente");
                else if (!users.contains(gestionaApp.buscaUserActivos(email))) {
                    users.add(gestionaApp.buscaUserActivos(email));
                    System.out.println("Usuario añadido");
                    cont++;
                } else System.out.println("Usuario añadido previamente");
            }
        } while (!email.equalsIgnoreCase("crear"));
        if (users.isEmpty() || users.size() == 1)
            System.out.println("No se puede crear un chat para ti mismo ni con 2 miembros");
        else {
            users.add(uTemp);
            if (gestionaApp.buscaChat(users) == null) {
                gestionaApp.addChat(users, uTemp);
                System.out.println("Chat creado");
                Utils.pulsaParaContinuar();
                Utils.limpiaPantalla();
                gestionaApp.addMensajeBienvenidaGrupo(uTemp.getEmail(),gestionaApp.getChats().getFirst().getId());
                usaChat(gestionaApp.buscaChat(users), gestionaApp, uTemp);

            } else System.out.println("Chat ya existente");
        }

    }

    //Crea un chat con una unica persona
    public static void crearChat(GestionaApp gestionaApp, User uTemp) {
        String email = preguntaPers("Introduce el nombre del usuario");
        if (email.equals(uTemp.getEmail())) System.out.println("No puedes crear un chat contigo mismo");
        else if (gestionaApp.buscaUserActivos(email) != null) {
            ArrayList<User> users = new ArrayList<>();
            users.add(uTemp);
            users.add(gestionaApp.buscaUserActivos(email));
            if (gestionaApp.buscaChat(users) == null) {
                if (gestionaApp.addChat(users, null)) {
                    Utils.limpiaPantalla();
                    usaChat(gestionaApp.buscaChats(uTemp).getFirst(), gestionaApp, uTemp);
                } else {
                    System.out.println("Error al crear el chat comprueba la conexión");
                }
            } else usaChat(gestionaApp.buscaChat(users), gestionaApp, uTemp);
        } else {
            System.out.println("Usuario no existente");
            Utils.pulsaParaContinuar();
            Utils.limpiaPantalla();
        }
    }

    //Borra una combersación
    public static void borrarChat(GestionaApp gestionaApp, User uTemp) {
        Chat chat;
        do {
            chat = seleccionaChat(gestionaApp, uTemp);
            if (chat != null) {
                //gestionaApp.borrarChat(chat, uTemp);
                gestionaApp.eliminaChat(chat,uTemp);
                System.out.println("Chat borrado");
            }
        } while (chat != null);
    }

    //Borrar la cuenta y con ello todos los chats
    public static void borrarCuenta(GestionaApp gestionaApp, User uTemp) {
        gestionaApp.borrarCuenta(uTemp);
    }

    //Menu de admin
    public static String pintaMenuAdmin() {
        System.out.println("""
                1. Hacer admin
                2. Añadir usuario
                3. Quitar admin
                4. Expulsar usuario
                5. Salir""");
        return preguntaPers("Introduce una opción");
    }
    //Gestión del menu admin
    public static void menuAdmin(Chat chat, User uTemp, GestionaApp gestionaApp) {
        String op;
        Utils.limpiaPantalla();
        if (chat.comprobarUserAdmin(uTemp)) {
            do {
                op = pintaMenuAdmin();
                switch (op) {
                    case "1":
                        hacerAdmin(chat);
                        break;
                    case "2":
                        addUser(chat,gestionaApp);
                        break;
                    case "3":
                        quitarAdmin(chat,uTemp);
                        break;
                    case "4":
                        eliminarUser(chat,uTemp,gestionaApp);
                        break;
                    case "5":
                        break;
                    default:
                        System.out.println("Opción no existente");
                        break;
                }
                Utils.limpiaPantalla();
            } while (!op.equals("5"));
        } else{
            System.out.println("No eres admin del grupo");
            Utils.pulsaParaContinuar();
            Utils.limpiaPantalla();
        }
    }
    //Selección de un usuario
    public static User seleccionaUser(ArrayList<User> users){
        int op;
        do {
            pintaUsers(users);
            try {
                op = Integer.parseInt(preguntaPers("Introduce una opción"));
            } catch (NumberFormatException e) {
                System.out.println("Opción no existente");
                op = Integer.MIN_VALUE;
            }
            if (op <= users.size()) {
                if (op <= 0) System.out.println("Opción no existente");
                else {
                    Utils.limpiaPantalla();
                    return users.get(op - 1);
                }
            }
        } while (op != users.size() + 1);
        return null;
    }
    //Pintar los usuarios
    public static void pintaUsers(ArrayList<User> users){
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i));
        }
        System.out.println((users.size() + 1) + ". Salir");
    }
    //Hace admin a un usuario
    public static void hacerAdmin(Chat chat){
        User user;
        do{
            user = seleccionaUser(chat.getUsersNoAdmins());
            if (user != null) {
                chat.addUserAdmin(user);
            }
        }while(user != null);
    }
    //quita de admin a un usuario
    public static void quitarAdmin(Chat chat,User uTemp){
        User user;
        do{
            user = seleccionaUser(chat.getUsersAdmins(uTemp));
            if (user != null) {
                chat.quitarUserAdmin(user);
            }
        }while(user != null);
    }
    public static void addUser(Chat chat,GestionaApp gestionaApp){
        String user = preguntaPers("Introduce el nombre del nuevo usuario");
        if (chat.buscaUser(user) != null) System.out.println("Ese usuario ya esta en el grupo");
        else if(gestionaApp.buscaUserActivos(user) == null) System.out.println("Ese usuario no existe");
        else{
            chat.addUser(gestionaApp.buscaUserActivos(user));
            System.out.println("Usuario añadido");
        }
        Utils.pulsaParaContinuar();
    }
    public static void eliminarUser(Chat chat,User uTemp,GestionaApp gestionaApp){
        User user;
        do{
           user = seleccionaUser(chat.getUsuarios(uTemp.getEmail()));
           if (user != null){
               if (chat.comprobarUserAdmin(user)) chat.quitarUserAdmin(user);
               chat.borraUser(user.getEmail());
               gestionaApp.borraChat(chat.getId());
               System.out.println(user.getEmail() + " ha sido eliminado");
               Utils.pulsaParaContinuar();
               Utils.limpiaPantalla();
           }
        }while(user != null);
    }
}
