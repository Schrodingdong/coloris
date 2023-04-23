package ensias.android.coloris.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import ensias.android.coloris.R;

public class ColorSegmentationPopup implements IPopup{
    Context applicationContext;
    View rootView;
    Bundle bundle;
    private final String TAG = "COLORSEGMENTATIONPOPUP";
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
        View darkBg = rootView.findViewById(R.id.popup_dark_background);
        PopupWindow popupWindow = initialisePopupWindow(popupView);

        // show popup
        showPopup(darkBg, popupView, popupWindow);

        // set image in the popup
        ImageView segmentedImageView = popupView.findViewById(R.id.segmentedImage);
        segmentedImageView.setImageURI(imageUri);

        // get imageClick location
        segmentedImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                Log.d(TAG, String.format("Where we touched : {x : %d, y : %d}",x,y));
                Color pixelColor = getColor(x, y, segmentedImageView);
                Log.d(TAG, "pixelValue :" + pixelColor);
                return true;
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkBg.setVisibility(View.INVISIBLE);
                crossfade(darkBg, darkBg.getAlpha(),0);
                crossfade(popupView, popupView.getAlpha(),0);
            }
        });
    }

    private static Color getColor(int x, int y, ImageView segmentedImageView) {
        Bitmap bm = ((BitmapDrawable) segmentedImageView.getDrawable()).getBitmap();
        int pixel = bm.getPixel(x, y);
        Color pixelColor = Color.valueOf(pixel);
        return pixelColor;
    }

    @NonNull
    private PopupWindow initialisePopupWindow(View popupView) {
        int w = ViewGroup.LayoutParams.WRAP_CONTENT;
        int h = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, w, h, focusable);
        return popupWindow;
    }

    @NonNull
    private void showPopup(View darkBg, View popupView, PopupWindow popupWindow) {
        darkBg.setVisibility(View.VISIBLE);
        crossfade(darkBg,0,0.6f);
        popupWindow.showAtLocation(rootView, Gravity.CENTER,0,-120);
        crossfade(popupView,0,1);
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
