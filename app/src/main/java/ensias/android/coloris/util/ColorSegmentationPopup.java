package ensias.android.coloris.util;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.opencsv.CSVReader;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ensias.android.coloris.R;
import ensias.android.coloris.ui.hueShifter.HueShifterFragment;
import ensias.android.coloris.ui.hueShifter.TextViewAdapter;

public class ColorSegmentationPopup implements IPopup {
    Context applicationContext;
    View rootView;
    Bundle bundle;
    private final String TAG = "COLORSEGMENTATIONPOPUP";
    private TextToSpeech text2SpeechColor;
    private TextToSpeech text2SpeechHex;

    private String rgbValue, hexValue;
    private TextView rgbValueTextView;
    private TextView hexValueTextView;
    private TextView colorNameTextView;
    private TextView colorHexTextView;
    private TextView incompatibleColorBlindnessTextView;
    private Button buttonColorName;
    private Button buttonHex;

    private Button saveButton;
    HueShifterFragment colorPalette = new HueShifterFragment();

    public ColorSegmentationPopup(Context applicationContext, View rootView, Bundle bundle) {
        this.applicationContext = applicationContext;
        this.rootView = rootView;
        this.bundle = bundle;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void createPopup() {
        Uri imageUri = Uri.parse(bundle.get(PopupBundleKeys.IMAGE_URI.name()).toString());
        LayoutInflater inflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.fragment_popup_color_detector, null);
        View darkBg = rootView.findViewById(R.id.popup_dark_background);
        buttonColorName = popupView.findViewById(R.id.buttonColorName);
        buttonHex = popupView.findViewById(R.id.buttonHex);
        rgbValueTextView = popupView.findViewById(R.id.rgbValue);
        hexValueTextView = popupView.findViewById(R.id.hexValue);
        incompatibleColorBlindnessTextView = popupView.findViewById(R.id.incompatibleColorblindness);
        // initialise popup
        PopupWindow popupWindow = initialisePopupWindow(popupView);

        // show popup
        showPopup(darkBg, popupView, popupWindow);

        Bitmap bitmap = meanshift(imageUri);
        colorNameTextView = popupView.findViewById(R.id.color);
        colorHexTextView = popupView.findViewById(R.id.hexValue);

        saveButton = popupView.findViewById(R.id.button);
        setViews(colorPalette.getAdapter());

        // initialise TTS
        initTextToSpeech();

        // initialise TTS buttons
        buttonColorName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tellColor(colorNameTextView);
            }
        });
        buttonHex.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String colorCodes = getColorCodes();
                tellHex(colorCodes);
            }
        });

        // set image in the popup
        ImageView segmentedImageView = popupView.findViewById(R.id.segmentedImage);
        segmentedImageView.setImageBitmap(bitmap);

        // get imageClick location
        segmentedImageView.setOnTouchListener((View view, MotionEvent motionEvent) -> {
            // get imageView touched X,Y.
            int viewX = (int) motionEvent.getX();
            int viewY = (int) motionEvent.getY();
            Log.d(TAG, String.format("Where we touched : {x : %d, y : %d}", viewX, viewY));

            int viewWidth = segmentedImageView.getWidth();
            int viewHeight = segmentedImageView.getHeight();
            Bitmap image = ((BitmapDrawable) segmentedImageView.getDrawable()).getBitmap();
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            int x = (int) ((float) viewX * ((float) imageWidth / (float) viewWidth));
            int y = (int) ((float) viewY * ((float) imageHeight / (float) viewHeight));
            int pixel = image.getPixel(x, y);
            Log.d(TAG, String.format("Real touched image coordinates: {x : %d, y : %d}", x, y));

            // Get color codes : RGB and HEX
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            rgbValue = "R: " + red + ", G: " + green + ", B: " + blue;
            hexValue = "#" + Integer.toHexString(pixel);
            rgbValueTextView.setText(rgbValue);
            hexValueTextView.setText("HEX : " + hexValue);

            // get color name from csv.
            String s = colorName(red, green, blue);
            colorNameTextView.setText(s);
            Log.d("COLOR: ", s);

            // get incompatible color blindness
            String incompatibleColorBlindnessType = getIncompatibleColorBlindness(red, green, blue);
            incompatibleColorBlindnessTextView.setText(incompatibleColorBlindnessType);

            return true;
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkBg.setVisibility(View.INVISIBLE);
                crossfade(darkBg, darkBg.getAlpha(), 0);
                crossfade(popupView, popupView.getAlpha(), 0);
            }
        });
    }

    public void setViews(TextViewAdapter adapter) {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String colorName = colorNameTextView.getText().toString();
                DataObjectSingleton.addToListOfColorNames(colorName);
                String hex = colorHexTextView.getText().toString().substring(6);
                DataObjectSingleton.addToListOfColorHex(hex);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private String getColorCodes() {
        if (rgbValue == null || hexValue == null) {
            return "";
        }
        return rgbValue + ". " + hexValue;
    }

    private String getIncompatibleColorBlindness(int red, int green, int blue) {
        int maxValue = Math.max(Math.max(red, green), blue);
        if (maxValue == red)
            return ColorBlindness.PROTOANOMALY.name();
        else if (maxValue == green)
            return ColorBlindness.DEUTERANOMALY.name();
        else if (maxValue == blue)
            return ColorBlindness.TRITOANOMALY.name();
        return null;
    }

    private void initTextToSpeech() {

        text2SpeechColor = new TextToSpeech(applicationContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = text2SpeechColor.setLanguage(Locale.CANADA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "Language unfortunetlly not supported");
                    } else {
                        buttonColorName.setEnabled(true);
                    }
                }
            }
        });

        text2SpeechHex = new TextToSpeech(applicationContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result = text2SpeechHex.setLanguage(Locale.CANADA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "Language unfortunetlly not supported");
                    } else {
                        buttonHex.setEnabled(true);
                    }
                }
            }
        });
    }

    private void tellColor(TextView textView) {
        text2SpeechColor.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
        text2SpeechColor.setSpeechRate(0.50f);
    }

    private void tellHex_fromView(TextView textView) {
        text2SpeechHex.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
        text2SpeechHex.setSpeechRate(0.50f);
    }

    private void tellHex(String colorToSay) {
        if (colorToSay == null || colorToSay.isEmpty()) {
            Toast.makeText(applicationContext, "No color selected", Toast.LENGTH_SHORT).show();
            return;
        }
        text2SpeechHex.speak(colorToSay, TextToSpeech.QUEUE_FLUSH, null);
        text2SpeechHex.setSpeechRate(0.50f);
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
        crossfade(darkBg, 0, 0.6f);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, -120);
        crossfade(popupView, 0, 1);
    }

    private void crossfade(View view, float startAlpha, float finalAlpha) {
        int shortAnimationDuration = applicationContext.getResources()
                .getInteger(android.R.integer.config_shortAnimTime);
        view.setAlpha(startAlpha);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(finalAlpha)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    private Bitmap meanshift(Uri uriImage) {
        Log.i(TAG, "createPopup: started meanshift");
        Bitmap bitmap = null;

        // get source from uri.
        ImageDecoder.Source source = ImageDecoder.createSource(applicationContext.getContentResolver(), uriImage);
        try {
            // decode source to bitmap.
            bitmap = ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mat src = new Mat();
        Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp, src);

        // convert mat to 8-bit and 3 channel.
        Mat src8Bit3Channel = new Mat();
        Imgproc.cvtColor(src, src8Bit3Channel, Imgproc.COLOR_BGRA2RGB);
        src.release();

        // resize mat.
        Mat resized = new Mat();
        Imgproc.resize(src8Bit3Channel, resized, new Size(250, 250));
        src8Bit3Channel.release();

        // reduce noise.
        Mat noise = new Mat();
        Photo.fastNlMeansDenoisingColored(resized, noise, 3, 7, 7, 21);
        resized.release();

        // apply color mean-shift
        Mat dst = new Mat();
        Imgproc.pyrMeanShiftFiltering(noise, dst, 10.0, 20.0, 1,
                new TermCriteria(TermCriteria.MAX_ITER | TermCriteria.EPS, 50, 0.001));
        noise.release();

        // convert 8-bit 3 channel to RGBA
        Mat img2show = new Mat();
        Imgproc.cvtColor(dst, img2show, Imgproc.COLOR_BGR2RGBA);
        dst.release();

        // create bitmap from mat
        Bitmap out = Bitmap.createBitmap(img2show.width(), img2show.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img2show, out);
        img2show.release();

        Log.i(TAG, "createPopup: ended meanshift");
        return out;
    }

    private String colorName(int R, int G, int B) {
        InputStreamReader is;
        int minimum = 1000;
        int distance = 0;
        String cName = "";

        try {
            is = new InputStreamReader(applicationContext.getAssets().open("colors.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // read csv.
        CSVReader csv = new CSVReader(is);
        String[] line;
        try {
            while ((line = csv.readNext()) != null) {
                int r = Integer.valueOf(line[3]);
                int g = Integer.valueOf(line[4]);
                int b = Integer.valueOf(line[5]);
                distance = Math.abs(R - r) + Math.abs(G - g) + Math.abs(B - b);
                if (distance <= minimum) {
                    minimum = distance;
                    cName = line[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cName;
    }

    @Override
    public void onDestroy() {
        if (text2SpeechColor != null) {
            text2SpeechColor.stop();
            text2SpeechColor.shutdown();
        }
        if (text2SpeechHex != null) {
            text2SpeechHex.stop();
            text2SpeechHex.shutdown();
        }
    }

}