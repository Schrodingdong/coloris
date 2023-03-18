package ensias.android.coloris.ui.colorDetector;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.impl.CameraConfig;
import androidx.camera.core.impl.CameraInternal;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.LinkedHashSet;

import ensias.android.coloris.R;
import ensias.android.coloris.databinding.FragmentColorDetectorBinding;

public class ColorDetectorFragment extends Fragment {

    private static final String TAG = "ColorDetectorFragment";
    private FragmentColorDetectorBinding binding;
    // Views :
    private View root;
    private View bottomSheet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the model class - Unused for now
        ColorDetectorViewModel colorDetectorViewModel =
                new ViewModelProvider(this).get(ColorDetectorViewModel.class);
        // initialise the elements
        root = initialiseRoot(inflater, container);
        bottomSheet = initialiseBottomSheet();
        // Return root
        return root;
    }

    @NonNull
    private View initialiseRoot(LayoutInflater inflater, ViewGroup container){
        binding = FragmentColorDetectorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    private View initialiseBottomSheet(){
        View bottomSheet = root.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(500);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Log.d(TAG, "[INFO] - Successfully initialised BottomSheet !");
        return bottomSheet;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}