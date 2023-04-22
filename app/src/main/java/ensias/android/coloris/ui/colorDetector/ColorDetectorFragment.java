package ensias.android.coloris.ui.colorDetector;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.impl.CameraConfig;
import androidx.camera.core.impl.CameraInternal;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedHashSet;

import ensias.android.coloris.MainActivity;
import ensias.android.coloris.R;
import ensias.android.coloris.customservices.AppServices;
import ensias.android.coloris.customservices.CameraService;
import ensias.android.coloris.databinding.FragmentColorDetectorBinding;

public class ColorDetectorFragment extends Fragment {

    private static final String TAG = "ColorDetectorFragment";
    private FragmentColorDetectorBinding binding;
    // Views :
    private View root;
    private View bottomSheet;

    private FloatingActionButton takePictureButton;
    private static CameraService cameraService;
    private AppServices appServices;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the model class - Unused for now
        ColorDetectorViewModel colorDetectorViewModel =
                new ViewModelProvider(this).get(ColorDetectorViewModel.class);


        // initialise the elements
        root = initialiseRoot(inflater, container);
//        bottomSheet = initialiseBottomSheet();
        takePictureButton = initialiseFABpicture();

        // Using a Custom Service Class
        cameraService = new CameraService(this, binding);
        appServices = new AppServices(this);
        appServices.addNewService(cameraService);
        appServices.startAllServices();

        // Return root
        return root;
    }

    @NonNull
    private View initialiseRoot(LayoutInflater inflater, ViewGroup container){
        binding = FragmentColorDetectorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
//    private View initialiseBottomSheet(){
//        View bottomSheet = root.findViewById(R.id.bottom_sheet);
//        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        bottomSheetBehavior.setPeekHeight(500);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        Log.d(TAG, "[INFO] - Successfully initialised BottomSheet !");
//        return bottomSheet;
//    }
    private FloatingActionButton initialiseFABpicture(){
        FloatingActionButton fab = binding.buttonTakePicture;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivity(intent);
                ColorDetectorFragment.cameraService.takePicture();
            }
        });
        return fab;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}