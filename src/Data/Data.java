package Data;

import java.time.LocalDateTime;

public class Data {
    public static String mensajeCreacion(String emailCreador){
        return emailCreador + " creo el grupo el " + LocalDateTime.now().getDayOfMonth() + "/" + LocalDateTime.now().getMonth() + "/" + LocalDateTime.now().getYear() + "A las " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute();
    }

}
