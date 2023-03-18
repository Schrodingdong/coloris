package ensias.android.coloris;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;

import com.sarnava.textwriter.TextWriter;

import ensias.android.coloris.databinding.ActivityMainBinding;
import ensias.android.coloris.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";

    private ActivitySplashScreenBinding binding;
    private TextWriter textWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get binding
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());

        // set the view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        // configure TextWriter
        textWriter = binding.titleTextWriter;
        textWriter
                .setWidth(8)
                .setDelay(30)
                .setColor(Color.WHITE)
                .setConfig(TextWriter.Configuration.SQUARE)
                .setSizeFactor(50f)
                .setLetterSpacing(50f)
                .setText("COLORIS")
                .setListener(new TextWriter.Listener() {
                    @Override
                    public void WritingFinished() {
                        new CountDownTimer(1000,1000) {
                            @Override
                            public void onFinish() {
                                // Goto Main activity
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                // finish the SplashScreen activity, no longer in the history stack
                                finish();
                            }

                            @Override
                            public void onTick(long l) {}
                        }.start();

                    }
                })
                .startAnimation();

    }
}