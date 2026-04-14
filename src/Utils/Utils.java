package Utils;

import java.time.LocalDateTime;
import java.util.Scanner;

public class Utils {
    public static Scanner SCANNER = new Scanner(System.in);
    public static LocalDateTime generaFechaAleatoria(){
        int segundos,minutos,hora,dia,mes;
        segundos = (int) (Math.random() * 60);
        minutos = (int) (Math.random() * 60);
        hora = (int) (Math.random() * 24);
        mes = (int) (Math.random() * 4 + 1);
        switch (mes){
            case 1,3,5:
                dia = (int) (Math.random() * 31 + 1);
                break;
            case 2:
                dia = (int) (Math.random() * 28 + 1);
                break;
            default:
                dia = (int) (Math.random() * 30 + 1);
                break;
        }
        LocalDateTime fecha = LocalDateTime.of(2026,mes,dia,hora,minutos,segundos);
        if (fecha.isAfter(LocalDateTime.now())) return LocalDateTime.of(2026,LocalDateTime.now().getMonth(),((LocalDateTime.now().getDayOfMonth() - 1 <= 0 ? 1: LocalDateTime.now().getDayOfMonth() - 1)), fecha.getHour(), fecha.getMinute(), fecha.getSecond());
        return fecha;
    }
    public static void limpiaPantalla(){
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }
    public static void pulsaParaContinuar(){
        System.out.println("Pulsa para continuar...");
        SCANNER.nextLine();
    }
    public static String pasaFechaString(LocalDateTime fecha){
        return fecha.getYear() + "/" + (fecha.getMonthValue() < 10 ? "0" + fecha.getMonthValue(): fecha.getMonthValue()) + "/" + fecha.getDayOfMonth() + "/" + fecha.getHour() +"/"+ fecha.getMinute() + "/" + fecha.getSecond();
    }
}
