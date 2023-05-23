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

public class TextViewAdapter extends ArrayAdapter<String> {
    private List<String> colorNames;
    private List<String> colorHex;

    public TextViewAdapter(Context context, int resource, List<String> colorNames, List<String> colorHex){
        super(context, resource);
        this.colorNames = colorNames;
        this.colorHex = colorHex;
    }

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        String text = getItem(position);
//        Log.d("ADAPTER", text);
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext())
//                    .inflate(android.R.layout.simple_list_item_1, parent, false);
//            Log.d("ADAPTER", convertView.toString());
//
//        }
//        ((TextView) convertView).setText(text);
//        return convertView;
//
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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
