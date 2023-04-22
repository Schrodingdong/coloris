package ensias.android.coloris.customservices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

import ensias.android.coloris.R;
import ensias.android.coloris.databinding.ActivityMainBinding;
import ensias.android.coloris.databinding.FragmentColorDetectorBinding;
import ensias.android.coloris.ui.colorDetector.ColorDetectorFragment;


/*
* Custom Camera Class to instantiate the Preview
* TODO : Add the ImageAnalysis thingie mn tutorial
* */

public class CameraService implements CustomService{
    private static final String TAG = "CameraService";
    private final String[] CAMERA_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private Context applicationContext;
    private LifecycleOwner applicationLifeCycleOwner;
    private FragmentColorDetectorBinding binding;
    private ImageCapture imageCapture;
    public CameraService(ColorDetectorFragment mActivity, FragmentColorDetectorBinding binding){
        this.applicationContext = mActivity.getContext();
        this.applicationLifeCycleOwner = mActivity;
        this.binding = binding;
    }

    // for starting the cameraPreview
    @Override
    public boolean startService() {// Copy paste code go brrrr
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(applicationContext);

        cameraProviderFuture.addListener(
                () -> initialiseCamera(cameraProviderFuture),
                ContextCompat.getMainExecutor(applicationContext)
        );
        return true;
    }

    private void initialiseCamera(ListenableFuture<ProcessCameraProvider> cameraProviderFuture) {
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

        // Select back camera as a default
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        // Configure for taking a photo
        imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(applicationContext.getDisplay().getRotation())
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setJpegQuality(20)
                        .build();

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll();

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                    applicationLifeCycleOwner, cameraSelector, imageCapture, preview);

        } catch (Exception exc) {
            Log.e(TAG, "Use case binding failed", exc);
        }
    }
    public void takePicture(){
        long timestamp = System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "takePicture: " + uri);
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions
                        .Builder(applicationContext.getContentResolver(), uri, contentValues)
                        .build();
        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(applicationContext),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Log.d(TAG, "Image Capture SUCCESS");
                        binding.testImageView.setImageURI(outputFileResults.getSavedUri());
                        File f = new File(outputFileResults.getSavedUri().getPath());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "ERROR : " + exception.getMessage());
                    }
                }
        );
    }
    @Override
    public String[] getServicePermissions() {
        return CAMERA_PERMISSIONS;
    }
}
