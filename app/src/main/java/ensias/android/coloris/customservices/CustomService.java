package ensias.android.coloris.customservices;

public interface CustomService {
    String[] getServicePermissions(); // returns a list for the needed permissions for the service
    boolean startService();
}
