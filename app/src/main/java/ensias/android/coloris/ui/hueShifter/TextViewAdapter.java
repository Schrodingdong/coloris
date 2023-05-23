package ensias.android.coloris.ui.hueShifter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ensias.android.coloris.R;

public class TextViewAdapter extends ArrayAdapter {
    private List<String> colorNames;
    private List<String> colorHex;

    public TextViewAdapter(Context context, int resource, List<String> colorNames, List<String> colorHex){
        super(context, resource, colorNames);
        Log.d("ADAPTER", "TextViewAdapter: QSDNKLGIGNQSDLG?QSL");
        this.colorNames = colorNames;
        this.colorHex = colorHex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("ADAPTER", colorNames.get(position) + " " + colorHex.get(position) + " " + position);
        View v;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.grid_item, null);

        TextView textView = (TextView) v.findViewById(R.id.colorName);
        View colorView = (View) v.findViewById(R.id.colorSquare);

        textView.setText(colorNames.get(position));
        colorView.setBackgroundColor(Color.parseColor(colorHex.get(position)));

        return v;

    }
}
