package pl.bresiu.minutnik;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    final String PREFERENCES_NAME = "Preferences";
    final String IS_WORKING = "isWorking";
    EditText interwal;
    Button startstop;
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        preferencesEditor = preferences.edit();
        setWidgets();
    }

    public void setWidgets() {
        interwal = (EditText) findViewById(R.id.interwal);
        startstop = (Button) findViewById(R.id.startstop);
        startstop.setText((isWorking()) ? "STOP" : "START");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startstop:
                if (!isWorking()) {
                    setWork(true);
                    Toast.makeText(getApplicationContext(), "Wystartowano", Toast.LENGTH_SHORT).show();
                    startstop.setText("STOP");
                    btnStartSchedule(v);
                } else {
                    setWork(false);
                    Toast.makeText(getApplicationContext(), "Zastopowano", Toast.LENGTH_SHORT).show();
                    startstop.setText("START");
                    btnCancelSchedules(v);
                }
                break;
        }
    }

    public int getInterwal() {
        return Integer.parseInt(interwal.getText().toString()) * 60000;
    }

    public boolean isWorking() {
        return preferences.getBoolean(IS_WORKING, false);
    }

    public void setWork(boolean work) {
        preferencesEditor.putBoolean(IS_WORKING, work);
        preferencesEditor.commit();
    }

    public void btnStartSchedule(View v) {

        try {
            AlarmManager alarms = (AlarmManager) this
                    .getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(getApplicationContext(),
                    AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.ACTION_ALARM,
                    AlarmReceiver.ACTION_ALARM);

            final PendingIntent pIntent = PendingIntent.getBroadcast(this,
                    1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarms.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), getInterwal(), pIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnCancelSchedules(View v) {

        Intent intent = new Intent(getApplicationContext(),
                AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.ACTION_ALARM,
                AlarmReceiver.ACTION_ALARM);

        final PendingIntent pIntent = PendingIntent.getBroadcast(this, 1234567,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarms = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);

        alarms.cancel(pIntent);
    }
}
