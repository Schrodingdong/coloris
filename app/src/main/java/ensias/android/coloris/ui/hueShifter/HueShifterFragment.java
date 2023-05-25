package ensias.android.coloris.ui.hueShifter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import ensias.android.coloris.R;
import ensias.android.coloris.databinding.FragmentColorPaletteBinding;
import ensias.android.coloris.util.ColorSegmentationPopup;
import ensias.android.coloris.util.DataObjectSingleton;

public class HueShifterFragment extends Fragment {

    private final String TAG = "COLORPALETTE";
    private FragmentColorPaletteBinding binding;
    private GridView gridView;
    private TextView textView;
    private Button saveButton;

    private ArrayList<String> colorNames;
    private TextViewAdapter adapter;

    public HueShifterFragment() {
        colorNames = new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_color_palette, container, false);
        gridView = root.findViewById(R.id.colorpalette_gv);
        adapter = new TextViewAdapter(getActivity(), R.layout.grid_item, DataObjectSingleton.getListOfColorNames(),
                DataObjectSingleton.getListOfColorHex());
        gridView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public ArrayList<String> getColorNames() {
        return colorNames;
    }

    public TextViewAdapter getAdapter() {
        return adapter;
    }

}