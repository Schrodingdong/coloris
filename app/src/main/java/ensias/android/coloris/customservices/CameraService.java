package ensias.android.coloris.customservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.net.ConnectException;
import java.util.Arrays;

import ensias.android.coloris.MainActivity;
import ensias.android.coloris.databinding.ActivityMainBinding;


/*
* Custom Camera Class to instantiate the Preview
* */

public class CameraService implements CustomService, ImageAnalysis.Analyzer{
    private static final String TAG = CameraService.class.getCanonicalName();
    private final String[] CAMERA_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private final Context applicationContext;
    private final Activity mainActivity;
    private final LifecycleOwner applicationLifeCycleOwner;
    private final ActivityMainBinding binding;

    public CameraService(MainActivity mActivity, ActivityMainBinding binding){
        this.applicationContext = mActivity;
        this.mainActivity = mActivity;
        this.applicationLifeCycleOwner = mActivity;
        this.binding = binding;
    }

    // for starting the cameraPreview
    @SuppressLint({"RestrictedApi", "NewApi"})
    @Override
    public boolean startService() {// Copy paste code go brrrr
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(applicationContext);

        cameraProviderFuture.addListener(() -> {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Preview
            Preview preview = new Preview.Builder().build();
            preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

            // Image Analysis Use Case :
            ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(applicationContext), this);

            // Select back camera as a default
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        applicationLifeCycleOwner, cameraSelector, preview/*, imageAnalysis*/);

            } catch (Exception exc) {
                Log.e(TAG, "Use case binding failed", exc);
            }
        }, ContextCompat.getMainExecutor(applicationContext));
        return true;
    }

    @Override
    public String[] getServicePermissions() {
        return CAMERA_PERMISSIONS;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        // iterative steps for image analysis on the camera preview
        // TODO implement the SAMPLE hue shifter
        Bitmap bitmap = binding.cameraPreview.getBitmap();
        image.close();

        if(bitmap == null)
            return;
        final Bitmap bitmapShiftedHue = changeHue(bitmap);


        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: UITHRED");
                binding.hueShiftPreview.setImageBitmap(bitmapShiftedHue);
            }
        });
    }


    public Bitmap changeResolution(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public Bitmap scaleDownBitmap(Bitmap bitmap, int maxImageSize) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth;
        int resizedHeight;

        float aspectRatio = (float) originalWidth / (float) originalHeight;

        if (originalWidth > originalHeight) {
            resizedWidth = maxImageSize;
            resizedHeight = Math.round(maxImageSize / aspectRatio);
        } else {
            resizedHeight = maxImageSize;
            resizedWidth = Math.round(maxImageSize * aspectRatio);
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
        return resizedBitmap;
    }
    @NonNull
    private Bitmap changeHue(@NonNull Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        BitmapFactory.Options options = new BitmapFactory.Options();



        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        bmpGrayscale = scaleDownBitmap(bmpGrayscale,800);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();

        // do shifting manipulation
        ColorMatrix cm = new ColorMatrix();
//        float[] newMatrixArr = {
//                -0.11f,0.29f ,-0.67f ,0 ,0.67f,
//                -0.08f,0.28f ,-0.29f ,0 ,0.57f,
//                0.02f ,-0.09f,-0.69f ,0 ,0.09f,
//                0     ,0     ,0      ,1 ,0};

        float[] newMatrixArr = {
                0,0,0,0,0,
                0,1,0,0,0,
                0,0,1,0,0,
                0,0,0,1,0
        };
        cm.set(newMatrixArr);
        Log.d(TAG, "changeHue: COLOR MATRIX : "+ Arrays.toString(cm.getArray()));
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(newMatrixArr);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }




}
