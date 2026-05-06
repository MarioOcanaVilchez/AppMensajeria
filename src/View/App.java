package View;

import Controller.GestionaApp;
import Models.Chat;
import Models.Mensaje;
import Models.User;
import Utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    static void main() {
        runApp();
    }
    public static Scanner scanner = new Scanner(System.in);

    public static void runApp() {
        GestionaApp gestionaApp = new GestionaApp();
        String op = null;
        User uTemp = gestionaApp.getUsuario();
        if (uTemp != null) gestionaApp.cargaChats();
        do {
            if (op == null){
                if (uTemp == null) uTemp = menuInicio(gestionaApp);
            } else uTemp = menuInicio(gestionaApp);
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
                            gestionaApp.eliminaUsuarioEnUso();
                            break;
                        default:
                            System.out.println("Opción no existente");
                    }
                    if (!op.equals("7")){
                        gestionaApp.guardaUser();
                        Utils.pulsaParaContinuar();
                        Utils.limpiaPantalla();
                    }
                } while (!op.equals("7") && uTemp.getId() != 0);
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
                    if (usuario != null){
                        gestionaApp.guardaUser();
                        return usuario;
                    }
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
    //Pregunta de si o no
    public static boolean preguntaSON(String mensaje){
        String op;
        do{
            op = preguntaPers(mensaje);
            if (!op.equalsIgnoreCase("si") && !op.equalsIgnoreCase("no")) System.out.println("Respuesta no válida responde con si o no");
        }while(!op.equalsIgnoreCase("si") && !op.equalsIgnoreCase("no"));
        return op.equalsIgnoreCase("si");
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
    public static void pintaChats(ArrayList<Chat> chats) {
        if (chats.isEmpty()) System.out.println("No hay chats");
        for (int i = 0; i < chats.size(); i++) {
            System.out.println((i + 1) + ". " + chats.get(i).getNombre());
        }
        System.out.println((chats.size() + 1) + ". Salir");
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
            if(gestionaApp.cambiaEmail(emailNuevo,uTemp)) System.out.println("Email actualizado");
            else System.out.println("Error de conexión intentalo de nuevo");
        }
    }

    //Cambiar la clave
    public static void cambiaClave(User uTemp, GestionaApp gestionaApp) {
        String claveNueva = preguntaPers("Introduce tu nueva contraseña");
        if(gestionaApp.cambiaClave(claveNueva,uTemp)) System.out.println("clave actualizado");
        else System.out.println("Error de conexión intentalo de nuevo");
    }

    //Menu para seleccionar un chat o grupo
    public static Chat seleccionaChat(GestionaApp gestionaApp) {
        int op;
        do {
            pintaChats(gestionaApp.getChats());
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
            chat = seleccionaChat(gestionaApp);
            if (chat != null) usaChat(chat, gestionaApp, uTemp);
        } while (chat != null);
    }
    //Usar un chat o grupo
    public static void usaChat(Chat chat, GestionaApp gestionaApp, User uTemp) {
        gestionaApp.cargaChats();
        gestionaApp.cambiaChatUso(chat.getId());
        pintaChat(chat);
        String mensaje;
        do {
            RecargaChat recargaChat = new RecargaChat();
            ExecutorService hilo = Executors.newSingleThreadExecutor();
            hilo.submit(recargaChat);
            mensaje = preguntaPers("mensaje o salir para volver al menú o admin para entrar en la configuración del grupo");
            hilo.shutdownNow();
            if (mensaje.equalsIgnoreCase("salir")){
                gestionaApp.cambiaChatUso(-1);
                gestionaApp.cargaChats();
                System.out.println("Volviendo al menú");
            } else if (mensaje.equalsIgnoreCase("admin")){
                menuAdmin(chat,uTemp,gestionaApp);
                gestionaApp.cargaChats();
                chat = gestionaApp.buscaChat(gestionaApp.getChatEnUso());
                Utils.limpiaPantalla();
                pintaChat(chat);
            } else {
                gestionaApp.addMensaje(chat, new Mensaje(uTemp, mensaje, chat.getId(), LocalDateTime.now()));
                Utils.limpiaPantalla();
                gestionaApp.cargaChats();
                chat = gestionaApp.buscaChat(gestionaApp.getChatEnUso());
                pintaChat(chat);
            }
        } while (!mensaje.equalsIgnoreCase("salir"));
    }

    //Pintar un chat
    public static void pintaChat(Chat chat) {
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
        String email,nombre;
        int cont = 1;
        do {
            email = preguntaPers("Introduce al miembro " + cont + " del chat o crear para pasar a la configuración del grupo o salir para cancelar");
            if (!email.equalsIgnoreCase("salir")) {
                if (!email.equalsIgnoreCase("crear")) {
                    if (gestionaApp.buscaUserActivos(email) == null) System.out.println("Usuario no existente");
                    else if (!users.contains(gestionaApp.buscaUserActivos(email))) {
                        users.add(gestionaApp.buscaUserActivos(email));
                        System.out.println("Usuario añadido");
                        cont++;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Utils.limpiaPantalla();
                    } else System.out.println("Usuario añadido previamente");
                }
            }
        } while (!email.equalsIgnoreCase("crear") && !email.equalsIgnoreCase("salir"));
        if (!email.equalsIgnoreCase("salir")) {
            if (users.isEmpty() || users.size() == 1)
                System.out.println("No se puede crear un chat para ti mismo ni con 2 miembros");
            else {
                users.add(uTemp);
                nombre = pideNombre();
                gestionaApp.addChat(users, uTemp, nombre, uTemp);
                System.out.println("Chat creado");
                Utils.pulsaParaContinuar();
                Utils.limpiaPantalla();
                gestionaApp.addMensajeBienvenidaGrupo(uTemp.getEmail(), gestionaApp.getChats().getFirst().getId());
                usaChat(gestionaApp.buscaChat(users), gestionaApp, uTemp);
            }
        } else System.out.println("Operación cancelada");
    }
    public static String pideNombre(){
        String nombre = null;
        if (preguntaSON("Desea ponerle un nombre al grupo")) nombre = preguntaPers("Introduce el nombre");
        return nombre;
    }


    //Crea un chat con una unica persona
    public static void crearChat(GestionaApp gestionaApp, User uTemp) {
        String email = preguntaPers("Introduce el nombre del usuario o salir para volver");
        if (!email.equalsIgnoreCase("salir")) {
            if (email.equals(uTemp.getEmail())) System.out.println("No puedes crear un chat contigo mismo");
            else if (gestionaApp.buscaUserActivos(email) != null) {
                ArrayList<User> users = new ArrayList<>();
                users.add(uTemp);
                users.add(gestionaApp.buscaUserActivos(email));
                if (gestionaApp.buscaChat(users) == null) {
                    if (gestionaApp.addChat(users, null, null, uTemp)) {
                        Utils.limpiaPantalla();
                        usaChat(gestionaApp.buscaChats(uTemp).getFirst(), gestionaApp, uTemp);
                    } else {
                        System.out.println("Error al crear el chat comprueba la conexión");
                    }
                } else {
                    Utils.limpiaPantalla();
                    usaChat(gestionaApp.buscaChat(users), gestionaApp, uTemp);
                }
            } else {
                System.out.println("Usuario no existente");
                Utils.pulsaParaContinuar();
                Utils.limpiaPantalla();
            }
        } else System.out.println("Operación cancelada");
    }

    //Borra una combersación
    public static void borrarChat(GestionaApp gestionaApp, User uTemp) {
        Chat chat;
        do {
            chat = seleccionaChat(gestionaApp);
            if (chat != null) {
                if (gestionaApp.borrarChat(chat,uTemp)) System.out.println("Chat eliminado");
                else System.out.println("Error de conexión intentalo de nuevo");
            }
        } while (chat != null);
    }

    //Borrar la cuenta y con ello todos los chats
    public static void borrarCuenta(GestionaApp gestionaApp, User uTemp) {
        if (preguntaSON("Estas seguro perderás los todos tus chats y mensajes")){
            gestionaApp.borrarCuenta(uTemp);
            uTemp.setId(0);
        }
        else System.out.println("Operación cancelada");
    }

    //Menu de admin
    public static String pintaMenuAdmin() {
        System.out.println("""
                1. Cambiar nombre del grupo
                2. Hacer admin
                3. Añadir usuario
                4. Quitar admin
                5. Expulsar usuario
                6. Salir""");
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
                        cambiaNombreChat(gestionaApp, chat);
                        break;
                    case "2":
                        hacerAdmin(chat,gestionaApp);
                        break;
                    case "3":
                        addUser(chat,gestionaApp);
                        break;
                    case "4":
                        quitarAdmin(chat,uTemp,gestionaApp);
                        break;
                    case "5":
                        eliminarUser(chat,uTemp,gestionaApp);
                        break;
                    case "6":
                        break;
                    default:
                        System.out.println("Opción no existente");
                        break;
                }
                Utils.limpiaPantalla();
            } while (!op.equals("6"));
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
            System.out.println((i + 1) + ". " + users.get(i).getEmail());
        }
        System.out.println((users.size() + 1) + ". Salir");
    }
    //Hace admin a un usuario
    public static void hacerAdmin(Chat chat,GestionaApp gestionaApp){
        User user;
        do{
            user = seleccionaUser(chat.getUsersNoAdmins());
            if (user != null) {
                if (gestionaApp.addAdmin(chat,user)) System.out.println("El usuario " + user.getEmail() + " ahora es admin del grupo");
                else System.out.println("Error de conexión intentalo de nuevo");
            }
        }while(user != null);
    }
    //quita de admin a un usuario
    public static void quitarAdmin(Chat chat,User uTemp,GestionaApp gestionaApp){
        User user;
        do{
            user = seleccionaUser(chat.getUsersAdmins(uTemp));
            if (user != null) {
                if (gestionaApp.quitaAdmin(chat,user)) System.out.println("El usuario " + user.getEmail() + " ya no es admin del grupo");
                else System.out.println("Error de conexión intentalo de nuevo");
            }
        }while(user != null);
    }
    public static void addUser(Chat chat,GestionaApp gestionaApp){
        String user = preguntaPers("Introduce el nombre del nuevo usuario");
        if (chat.buscaUser(user) != null) System.out.println("Ese usuario ya esta en el grupo");
        else if(gestionaApp.buscaUserActivos(user) == null) System.out.println("Ese usuario no existe");
        else{
            if (gestionaApp.addUserChat(chat,gestionaApp.buscaUserActivos(user))) System.out.println("El usuario " + user + " ha sido añadido al grupo");
            else System.out.println("Error de conexión intentalo de nuevo");
        }
        Utils.pulsaParaContinuar();
    }
    public static void eliminarUser(Chat chat,User uTemp,GestionaApp gestionaApp){
        User user;
        do{
           user = seleccionaUser(chat.getUsuarios(uTemp.getEmail()));
           if (user != null){
               if (chat.comprobarUserAdmin(user)) chat.quitarUserAdmin(user);
               gestionaApp.eliminaUserChat(chat,user);
               System.out.println(user.getEmail() + " ha sido eliminado");
               Utils.pulsaParaContinuar();
               Utils.limpiaPantalla();
           }
        }while(user != null);
    }
    public static void cambiaNombreChat(GestionaApp gestionaApp, Chat chat){
        String nombre = preguntaPers("Introduce el nuevo nombre del grupo o salir para volver");
        if (nombre.equalsIgnoreCase("salir")) System.out.println("Operación cancelada");
        else if (nombre.equals(chat.getNombre())) System.out.println("El nombre no puede ser igual al anterior");
        else if (gestionaApp.cambiaNombreChat(chat,nombre)) System.out.println("Nombre cambiado");
        else System.out.println("Error de conexión intentalo de nuevo");
    }
}
