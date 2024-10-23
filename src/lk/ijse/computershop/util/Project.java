package lk.ijse.computershop.util;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Project {
    public static String loggerUserName;
    public static String loggerUserRank;
    public static boolean isAdmin;
    private static Pane pane;

    public static Pane getPane() {
        return pane;
    }

    public static void setPane(Pane pane) {
        Project.pane = pane;
    }
    public  static String generateNextId(String firstCharacter,String currentId){
        if(currentId != null){
            return String.format(firstCharacter+"%03d", Integer.parseInt(currentId.substring(1))+1);
        }
        return firstCharacter+"001";
    }
    public static ButtonType showError(Alert.AlertType type, String title, String contend){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(contend);
        Optional<ButtonType> buttonType = alert.showAndWait();
        return buttonType.get();
    }
    public static LocalDate sub(int n){
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,n);
        String result=dateFormat.format(calendar.getTime());
        return LocalDate.parse(result);
    }
    public static boolean isValidName(String name){
        Pattern pattern=Pattern.compile("^[a-zA-Z\\s]{3,50}$");
        Matcher matcher=pattern.matcher(name);
        return matcher.matches();
    }
    public static boolean isValidNic(String nic){
        return Pattern.compile("^[0-9]{9}[vVxX]||[0-9]{12}$").matcher(nic).matches();
    }
    public static boolean isValidTelephoneNumber(String telNumber){
        Pattern pattern=Pattern.compile("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$");
        Matcher matcher=pattern.matcher(telNumber);
        return matcher.matches();
    }
    public static boolean isValidEmail(String email){
        Pattern pattern=Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        Matcher matcher=pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isValidPrice(String price){
        return Pattern.compile("^(\\d+)||((\\d+\\.)(\\d){2})$").matcher(price).matches();
    }
}
