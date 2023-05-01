package ensias.android.coloris.util;

import static org.opencv.core.TermCriteria.COUNT;
import static org.opencv.core.TermCriteria.EPS;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
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

import com.opencsv.CSVReader;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStreamReader;

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

        Bitmap bitmap=meanshift(imageUri);

        // set image in the popup
        ImageView segmentedImageView = popupView.findViewById(R.id.segmentedImage) ;
        segmentedImageView.setImageBitmap(bitmap);
        // get imageClick location
        segmentedImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                int pixel=bitmap.getPixel(x,y);
                int red=Color.red(pixel);
                int green=Color.green(pixel);
                int blue=Color.blue(pixel);

                String hex="#"+Integer.toHexString(pixel);
                Log.d(TAG, String.format("Where we touched : {x : %d, y : %d}",x,y));
//                Color pixelColor = getColor(x, y, segmentedImageView);
                Log.d(TAG, "HexValue :" + hex);
                String s=colorName(red,green,blue);
                Log.d("COLOR: ",s);

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

    private Bitmap meanshift(Uri uriImage){
        Bitmap bitmap=null;
        ImageDecoder.Source source = ImageDecoder.createSource(applicationContext.getContentResolver(),uriImage);
        try {
            bitmap = ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Mat src=new Mat();
        Bitmap bmp=bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Utils.bitmapToMat(bmp,src);

        Mat src8Bit3Channel=new Mat();
        Imgproc.cvtColor(src,src8Bit3Channel,Imgproc.COLOR_BGRA2RGB);

        Mat dst=new Mat();
        Imgproc.pyrMeanShiftFiltering(src8Bit3Channel,dst,35.0,20.0,3,new TermCriteria(COUNT+EPS,10,0.9));
        Mat img2show=new Mat();
        Imgproc.cvtColor(dst,img2show,Imgproc.COLOR_BGRA2RGB);

        Bitmap out=Bitmap.createBitmap(img2show.width(),img2show.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img2show,out);
        return out;
    }

    private String colorName(int R,int G, int B){
        InputStreamReader is;
        int count=0;
        int minimum=1000;
        int distance=0;
        String cName="";
//        String s="";
        try {
            is=new InputStreamReader(applicationContext.getAssets().open("colors.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CSVReader csv=new CSVReader(is);
        String[] line;
        try{
            while ((line=csv.readNext())!=null){
                int r=Integer.valueOf(line[3]);
                int g=Integer.valueOf(line[4]);
                int b=Integer.valueOf(line[5]);
                distance=Math.abs(R-r)+Math.abs(G-g)+Math.abs(B-b);
                if (distance<=minimum){
                    minimum=distance;
                    cName=line[1];
                }
//                s="Color Name: "+line[1]+"R: "+line[2]+"G: "+line[3]+"B: "+line[4]+"Hex: "+line[5];
////                Log.d("CSV",s);
//                count++;

            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        return cName;
    }
}