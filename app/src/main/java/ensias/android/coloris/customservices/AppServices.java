package ensias.android.coloris.customservices;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ensias.android.coloris.MainActivity;

/*
* this Class is to aggregate all the different services that our app could use
* and start them after verifying the permissions relative to each Service.
* */

public class AppServices {
    private final String TAG = "AppServices";
    private final String[] requiredPermissions = new String[20]; // 20 : big number to hold all the possible permissions
    private final int REQUEST_CODE_PERMISSIONS = 10; // 10 : idk lmao

    private int permission_int = 0;
    private ArrayList<CustomService> appServices = new ArrayList<>();
    private Context applicationContext;
    private Activity applicationActivity;

    public AppServices(MainActivity mActivity){
        this.applicationContext = mActivity;
        this.applicationActivity = mActivity;
    }


    public AppServices addNewService(CustomService cs){
        appServices.add(cs);
        for (String permission : cs.getServicePermissions()){
            requiredPermissions[permission_int++] = permission;
        }
        return this;
    }

    public void startAllServices(){
        // check permissions
        boolean permAccess = allPermissionsGranted();
        // start services if true
        if(permAccess){
            for (CustomService s : appServices){
                s.startService();
            }
        }
    }

    private boolean allPermissionsGranted() {
        List<String> deniedPermissions = new ArrayList<>();
        // Collect the denied permission
        for (String perm : requiredPermissions){
            if (perm == null) break;
            if (ContextCompat.checkSelfPermission(applicationContext, perm) == PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(perm);
            }
        }
        // Request denied permissions IF we have denied ones, else just start the services
        if(deniedPermissions.size() != 0 ){
            String[] deniedPermissionsArray = deniedPermissions.toArray(new String[0]);
            ActivityCompat.requestPermissions(applicationActivity, deniedPermissionsArray, REQUEST_CODE_PERMISSIONS);
        }
        return true;
    }

}
