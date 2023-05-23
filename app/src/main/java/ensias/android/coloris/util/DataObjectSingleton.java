package ensias.android.coloris.util;

import java.util.ArrayList;
import java.util.List;

public class DataObjectSingleton {
    private static DataObjectSingleton instance = null;
    private static ArrayList<String> colorNames;
    private static ArrayList<String> colorHex;
    private DataObjectSingleton(){
        colorNames = new ArrayList<>();
    }
    public static DataObjectSingleton getInstance(){
        if (instance == null){
            instance = new DataObjectSingleton();
        }
        return instance;
    }


    public static void addToListOfColorNames(String colorName){
        colorNames.add(colorName);
    }
    public static  List<String> getListOfColorNames(){
        return colorNames;
    }

    public static void addToListOfColorHex(String hex){
        colorHex.add(hex);
    }
    public static  List<String> getListOfColorHex(){
        return colorHex;
    }


}
