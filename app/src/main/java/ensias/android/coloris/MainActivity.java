package ensias.android.coloris;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.opencv.android.OpenCVLoader;

import ensias.android.coloris.customservices.AppServices;
import ensias.android.coloris.customservices.CameraService;
import ensias.android.coloris.databinding.ActivityMainBinding;
import ensias.android.coloris.ui.colorDetector.ColorDetectorFragment;
import ensias.android.coloris.ui.hueShifter.HueShifterFragment;
import ensias.android.coloris.ui.wiki.WikiFragment;
import ensias.android.coloris.util.DataObjectSingleton;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private AppServices appServices;
    private BottomNavigationView navView;
    public static CameraService cameraService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataObjectSingleton.getInstance();

        if(OpenCVLoader.initDebug())
            Log.d("OpenCV","LOADED SUCCESS");
        else
            Log.d("OpenCV","LOADED ERR");
        // Get the biding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set the view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(binding.getRoot());

//        // Using a Custom Service Class
//        cameraService = new CameraService(this, binding);
//        appServices = new AppServices(this);
//        appServices.addNewService(cameraService);
//        appServices.startAllServices();

        // Navigation menu
        navView = binding.navView;
        navView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_color_detector:
                    setCurrentFragment(new ColorDetectorFragment());
                    break;
                case R.id.menu_hue_shift:
                    setCurrentFragment(new HueShifterFragment());
                    break;
                case R.id.menu_wiki:
                    setCurrentFragment(new WikiFragment());
                    break;
            }
            return true;
        });
    }

    private void setCurrentFragment(Fragment f){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.nav_host_fragment_activity_main, f)
                .commit();
    }
}
