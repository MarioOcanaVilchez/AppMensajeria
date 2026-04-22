package View;

import Controller.GestionaApp;
import Utils.Utils;


public class Pruebas implements Runnable{
//todo problemas muy serios
    //La ultima vez dio excepcion y dejaba de recargarse el chat
    @Override
    public void run() {
        try {
        GestionaApp gestionaApp = new GestionaApp();
        do {
            if (gestionaApp.buscaCambios()) {
                    gestionaApp.cargaChats();
                    if (!gestionaApp.ultimoMensajeUser()) {
                        for (int i = 0; i < gestionaApp.getChats().size(); i++) {
                            if (gestionaApp.getChats().get(i).getId() == gestionaApp.getChatEnUso()) {
                                Utils.limpiaPantalla();
                                App.pintaChat(gestionaApp.getChats().get(i));
                                System.out.print("mensaje o salir o admin: ");
                                i = gestionaApp.getChats().size();
                            }
                        }
                    }
            }
                Thread.sleep(1000);
        }while (true);
        } catch (InterruptedException e) {

        }
    }
}
