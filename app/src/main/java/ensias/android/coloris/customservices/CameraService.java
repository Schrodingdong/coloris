package ensias.android.coloris.customservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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

import ensias.android.coloris.MainActivity;
import ensias.android.coloris.databinding.ActivityMainBinding;


/*
* Custom Camera Class to instantiate the Preview
* */

public class CameraService implements CustomService, ImageAnalysis.Analyzer{
    private static final String TAG = CameraService.class.getCanonicalName();
    private final String[] CAMERA_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private Context applicationContext;
    private LifecycleOwner applicationLifeCycleOwner;
    private ActivityMainBinding binding;

    public CameraService(MainActivity mActivity, ActivityMainBinding binding){
        this.applicationContext = mActivity;
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
                        applicationLifeCycleOwner, cameraSelector, preview, imageAnalysis);

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
        image.close();
    }
}
