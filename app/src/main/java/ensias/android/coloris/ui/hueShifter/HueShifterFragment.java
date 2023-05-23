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

    private FragmentColorPaletteBinding binding;
    private GridView gridView;

    private TextView textView;
    private Button saveButton;


    private final String TAG = "COLORPALETTE";

    private ArrayList<String> colorNames;
    //public ArrayAdapter<String> adapter;
    private  TextViewAdapter adapter;
    /*private static String[] colors={"red","green","blue","fidoo"};*/
    public HueShifterFragment() {
        colorNames = new ArrayList<>();
    }
   /* public ArrayList<String> getColorNames() {
        return colorNames;
    }

    public void setColorNames(ArrayList<String> colorNames) {
        this.colorNames = colorNames;
    }*/



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*HueShifterViewModel hueShifterViewModel =
                new ViewModelProvider(this).get(HueShifterViewModel.class);

        binding = FragmentColorPaletteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();*/


        //View view = inflater.inflate(R.layout.fragment_color_palette, container, false);

        /*adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, colorNames);
        gridView = root.findViewById(R.id.colorpalette_gv);
        gridView.setAdapter(adapter);

        Log.d(TAG, "---------------------");
        for (String i : colorNames) {
            Log.d(TAG, i);
        }
        Log.d(TAG, "---------------------");*/

        View root = inflater.inflate(R.layout.fragment_color_palette, container, false);
        GridView gridView = root.findViewById(R.id.colorpalette_gv);

//        colorNames = new ArrayList<>();
        adapter = new TextViewAdapter(getActivity(), DataObjectSingleton.getListOfColorNames());
        gridView.setAdapter(adapter);

        Log.d(TAG, "---------------------");
        for (String i : DataObjectSingleton.getListOfColorNames()) {
            Log.d(TAG, i);
        }
        Log.d(TAG, "---------------------");

        if (adapter.getCount() > 0) {
            Log.d(TAG, "adapter full :D");
        } else {
            Log.d(TAG, "adapter null :((");
        }






        return root;
    }

    /*public ArrayAdapter<String> getAdapter() {
        return adapter;
    }*/
    /*public void addColorName(String colorName) {

        this.colorNames.add(colorName);
        Log.d(TAG, colorName);
        this.adapter.notifyDataSetChanged();
        Log.d(TAG, "UPDATED");




    }*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public ArrayList<String> getColorNames() {
        return colorNames;
    }

    public void setColorNames(ArrayList<String> colorNames) {
        this.colorNames = colorNames;
    }

    public TextViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(TextViewAdapter adapter) {
        this.adapter = adapter;
    }
}