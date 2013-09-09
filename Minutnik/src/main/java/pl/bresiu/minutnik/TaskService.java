package pl.bresiu.minutnik;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

public class TaskService extends IntentService {

    public TaskService() {
        super("TaskService");
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        Looper looper = Looper.getMainLooper();
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            public void run() {
                try {
                    Vibrator vibrator = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                    MediaPlayer sound = MediaPlayer.create(getBaseContext(), R.raw.sound);
                    sound.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
