package ensias.android.coloris.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import ensias.android.coloris.R;

public class ColorSegmentationPopup implements IPopup{
    Context applicationContext;
    View rootView;
    Bundle bundle;
    public ColorSegmentationPopup(Context applicationContext, View rootView, Bundle bundle) {
        this.applicationContext = applicationContext;
        this.rootView = rootView;
        this.bundle = bundle;
    }

    @Override
    public void createPopup() {
        Uri imageUri = Uri.parse(bundle.get(PopupBundleKeys.IMAGE_URI.name()).toString());
        LayoutInflater inflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_color_detector, null);

        int w = ViewGroup.LayoutParams.WRAP_CONTENT;
        int h = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, w, h, focusable);

        View darkBg = rootView.findViewById(R.id.popup_dark_background);
        darkBg.setVisibility(View.VISIBLE);
        crossfade(darkBg,0,0.6f);

        popupWindow.showAtLocation(rootView, Gravity.CENTER,0,0);
        crossfade(popupView,0,1);

        ImageView segmentedImageView = popupView.findViewById(R.id.segmentedImage);
        segmentedImageView.setImageURI(imageUri);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkBg.setVisibility(View.INVISIBLE);
                crossfade(darkBg, darkBg.getAlpha(),0);
                crossfade(popupView, popupView.getAlpha(),0);
            }
        });
    }

    private void crossfade(View view,float startAlpha, float finalAlpha) {
        int shortAnimationDuration = applicationContext.getResources()
                .getInteger(android.R.integer.config_shortAnimTime);
        view.setAlpha(startAlpha);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(finalAlpha)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }
}
