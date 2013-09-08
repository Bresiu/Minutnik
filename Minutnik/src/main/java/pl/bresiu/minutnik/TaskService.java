package pl.bresiu.minutnik;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.widget.Toast;

public class TaskService extends IntentService {

    public TaskService() {
        super("TaskService");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        Toast.makeText(getApplicationContext(), "Interwał", Toast.LENGTH_SHORT).show();
        Vibrator vibrator = (Vibrator) getSystemService(getBaseContext().VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        MediaPlayer sound = MediaPlayer.create(this, R.raw.sound);
        sound.start();
    }

}
