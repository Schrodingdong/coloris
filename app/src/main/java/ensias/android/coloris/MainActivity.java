package ensias.android.coloris;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ensias.android.coloris.customservices.AppServices;
import ensias.android.coloris.customservices.CameraService;
import ensias.android.coloris.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the biding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set the view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        // Check Camera Permissions
        allPermissionsGranted();

        // TODO add navigation transition
//        BottomNavigationView navView = binding.navView;
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_wiki, R.id.navigation_color_detector, R.id.navigation_hue_shifter)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
    }


    private void startCamera() {  // Copy paste code go brrrr
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Preview
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

            // Select back camera as a default
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview);

            } catch (Exception exc) {
                Log.e(TAG, "Use case binding failed", exc);
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void startServices(){
        // To start the necessary services at startup of the app.
        startCamera();
    }
    private boolean allPermissionsGranted() {
        List<String> deniedPermissions = new ArrayList<>();
        // Collect the denied permission
        for (String perm : REQUIRED_PERMISSIONS){
            if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(perm);
            }
        }
        // Request denied permissions IF we have denied ones, else just start the services
        if(deniedPermissions.size() != 0 ){
            String[] deniedPermissionsArray = deniedPermissions.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, deniedPermissionsArray, REQUEST_CODE_PERMISSIONS);
        } else {
            startServices();
        }
        return true;
    }

    // Callback to when we first accept the permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (allPermissionsGranted()) {
                startServices();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}