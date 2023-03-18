package ensias.android.coloris.customservices;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import ensias.android.coloris.MainActivity;
import ensias.android.coloris.databinding.ActivityMainBinding;

public class CameraService implements CustomService{
    private static final String TAG = "CameraService";
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

            // Select back camera as a default
            CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        applicationLifeCycleOwner, cameraSelector, preview);

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
}
