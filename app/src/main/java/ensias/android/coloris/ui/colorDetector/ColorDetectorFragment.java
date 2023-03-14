package ensias.android.coloris.ui.colorDetector;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import ensias.android.coloris.R;
import ensias.android.coloris.databinding.FragmentColorDetectorBinding;

public class ColorDetectorFragment extends Fragment {

    private FragmentColorDetectorBinding binding;
    private final String TAG = "[ColorDetectorFragment]";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Get the model class
        ColorDetectorViewModel colorDetectorViewModel =
                new ViewModelProvider(this).get(ColorDetectorViewModel.class);

        // Initialise the root element from the binding
        binding = FragmentColorDetectorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the bottom sheet
        View bottomSheet = root.findViewById(R.id.bottom_sheet);
        Log.d(TAG, "onCreateView: dsdsd");
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(500);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Log.d("[INFO]", "onCreateView: colordetector");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}