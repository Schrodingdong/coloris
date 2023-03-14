package ensias.android.coloris.ui.hueShifter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ensias.android.coloris.databinding.FragmentHueShifterBinding;

public class HueShifterFragment extends Fragment {

    private FragmentHueShifterBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HueShifterViewModel hueShifterViewModel =
                new ViewModelProvider(this).get(HueShifterViewModel.class);

        binding = FragmentHueShifterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textDashboard;
//        hueShifterViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}