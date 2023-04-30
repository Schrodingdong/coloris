package ensias.android.coloris.customservices;

import static org.opencv.core.TermCriteria.COUNT;
import static org.opencv.core.TermCriteria.EPS;

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
import android.widget.Toast;

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

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.ByteBuffer;

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

        imageCapture.takePicture(
                ContextCompat.getMainExecutor(applicationContext), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        Toast.makeText(applicationContext, "Captured with SUCCESS", Toast.LENGTH_SHORT).show();
                        ByteBuffer byteBuffer=image.getPlanes()[0].getBuffer();
                        byteBuffer.rewind();

                        byte[] bytes=new byte[byteBuffer.capacity()];
                        byteBuffer.get(bytes);

                        byte[] clonedBytes=bytes.clone();
                        Bitmap bitmap=BitmapFactory.decodeByteArray(clonedBytes,0,clonedBytes.length);

                        Mat src=new Mat();
                        Bitmap bmp=bitmap.copy(Bitmap.Config.RGB_565,true);
                        Utils.bitmapToMat(bmp,src);

                        Mat src8Bit3Channel=new Mat();
                        Imgproc.cvtColor(src,src8Bit3Channel,Imgproc.COLOR_BGRA2RGB);

                        Mat dst=new Mat();
                        Imgproc.pyrMeanShiftFiltering(src8Bit3Channel,dst,35.0,20.0,3,new TermCriteria(COUNT+EPS,10,0.9));
                        Mat img2show=new Mat();
                        Imgproc.cvtColor(dst,img2show,Imgproc.COLOR_BGRA2RGB);

                        Bitmap out=Bitmap.createBitmap(img2show.width(),img2show.height(),Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(img2show,out);

                        binding.testImageView.setRotation((float) image.getImageInfo().getRotationDegrees());
                        binding.testImageView.setImageBitmap(out);
                        image.close();


                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                }
        );
    }
    @Override
    public String[] getServicePermissions() {
        return CAMERA_PERMISSIONS;
    }
}
