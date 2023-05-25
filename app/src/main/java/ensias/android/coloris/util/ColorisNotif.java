package ensias.android.coloris.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ensias.android.coloris.MainActivity;
import ensias.android.coloris.R;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class ColorisNotif extends Service {

    private static final long INTERVAL_ONE_HOUR = 60 * 60 * 1000; // 1 hour in milliseconds
    private Timer timer;

    private static final String CHANNEL_ID = "coloris";
    private static final String CHANNEL_NAME = "coloris";
    private static final String CHANNEL_DESC = "coloris";
    private static final String apiKey = "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";

    public void onCreate() {
        // Service initialization code
        super.onCreate();
        timer = new Timer();
        Toast.makeText(this, "Service fun facts is working", Toast.LENGTH_SHORT).show();
        startAsyncTaskPeriodically();

    }

    private void startAsyncTaskPeriodically() {
        // let the service execute asyncTask every 1h
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // Execute your AsyncTask here
                MyTask myAsyncTask = new MyTask("blue");
                myAsyncTask.execute();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, INTERVAL_ONE_HOUR);
    }

    private void stopAsyncTask() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAsyncTask();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String addNotification(String contentText) {// DISPLAYING NOTIFICATION (??)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Coloris Fun Facts")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return "not granted";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// creating a notification channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        notificationManagerCompat.notify(1, builder.build());
        return "granted";// display the notification
    }
    ////////////////////////////////////////// CHATGPT
    ////////////////////////////////////////// STUFF//////////////////////////////////////////////////////////////

    private static final String CHATGPT_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public static String callChatGPTAPI(String color) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(CHATGPT_API_ENDPOINT).newBuilder();
        /* urlBuilder.addQueryParameter("prompt", prompt); */
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "    \"model\": \"gpt-3.5-turbo\",\n" +
                        "    \"messages\": [\n" +
                        "        {\n" +
                        "            \"role\": \"user\",\n" +
                        "            \"content\": \"give me a fun fact about the color" + color + "\"\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}"))
                .build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Response body is null");
            }
            BufferedSource source = responseBody.source();
            String stringResponse = source.readString(Charset.forName("UTF-8"));
            // String stringResponse = responseBody.toString();

            responseBody.close();
            return stringResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String extractTextFromChatGPTResponse(String response) {
        if (response == null) {
            return null;
        }
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() == 0) {
                throw new JSONException("No choices found in response: " + response);
            }
            JSONObject choice = choices.getJSONObject(0);
            JSONObject messageObject = choice.getJSONObject("message");
            String contentValue = messageObject.getString("content");
            return contentValue;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class MyTask extends AsyncTask<Void, Void, String> {
        private String color;
        private String contentText;

        public MyTask(String color) {
            this.color = color;
        }

        public String getContentText() {
            return contentText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                contentText = extractTextFromChatGPTResponse(callChatGPTAPI(color));
                return contentText;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            addNotification(result);
        }

    }
}
