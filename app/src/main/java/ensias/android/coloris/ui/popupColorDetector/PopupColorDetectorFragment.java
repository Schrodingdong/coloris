/*
package ensias.android.coloris.ui.popupColorDetector;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ensias.android.coloris.R;
import ensias.android.coloris.ui.hueShifter.HueShifterFragment;

public class PopupColorDetectorFragment extends Fragment {

    private PopupColorDetectorFragment binding;
    private TextView colorTextView;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popup_color_detector, container, false);

        colorTextView = view.findViewById(R.id.color);
        saveButton = view.findViewById(R.id.button_save_color);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String colorName = colorTextView.getText().toString();
                HueShifterFragment paletteFragment = (HueShifterFragment) getParentFragmentManager().findFragmentById(R.id.gridContainer);
                Log.d(TAG, "waaaaaa33333333333333333333333333333333333333333");
                if (paletteFragment != null) {
                    paletteFragment.addColorName(colorName);
                    Log.d(TAG, "we good");

                }
            }
        });

        return view;

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
*/
