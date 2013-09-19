package pl.bresiu.minutnik;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//TODO: wyłaczanie service!

public class MainActivity extends Activity implements View.OnClickListener {
    final String ALARMS = "alarms";
    final String PREFERENCES_NAME = "preferences";
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;
    private EditText editMarch;
    private EditText editRun;
    private EditText editRepeats;
    private Button btnStartStop;
    private Button button;
    private Button button3;
    private Button button5;
    private ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
    private AlarmManager[] alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        preferencesEditor = preferences.edit();
        setWidgets();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startstop:
                if (isWorking()) {
                    preferencesEditor.putInt(ALARMS, 0);
                    btnStartStop.setText(getString(R.string.start));
                    deleteSchedule();
                } else {
                    btnStartStop.setText(getString(R.string.stop));
                    makeSchedule();
                }
                break;
            case R.id.zero:
                editMarch.setText(getString(R.string.one));
                editRun.setText(getString(R.string.one));
                editRepeats.setText(getString(R.string.one));
                break;
            case R.id.button:
                if (isInt(editMarch)) {
                    setM(editMarch, button);
                }
                break;
            case R.id.button2:
                if (isInt(editMarch)) {
                    setP(editMarch, button);
                }
                break;
            case R.id.button3:
                if (isInt(editRun)) {
                    setM(editRun, button3);
                }
                break;
            case R.id.button4:
                if (isInt(editRun)) {
                    setP(editRun, button3);
                }
                break;
            case R.id.button5:
                if (isInt(editRepeats)) {
                    setM(editRepeats, button5);
                }
                break;
            case R.id.button6:
                if (isInt(editRepeats)) {
                    setP(editRepeats, button5);
                }
                break;
        }
    }

    private void setWidgets() {
        editMarch = (EditText) findViewById(R.id.editMinutesOfMarch);
        editRun = (EditText) findViewById(R.id.editMinutesOfRun);
        editRepeats = (EditText) findViewById(R.id.editNumberOfRepeats);
        btnStartStop = (Button) findViewById(R.id.startstop);
        btnStartStop.setText((isWorking()) ? getString(R.string.stop) : getString(R.string.start));
        button = (Button) findViewById(R.id.button);
        button3 = (Button) findViewById(R.id.button3);
        button5 = (Button) findViewById(R.id.button5);
    }

    private List<Integer> makeList() {

        if (isInt(editMarch) && isInt(editRun) && isInt(editRepeats)) {
            List<Integer> list = new ArrayList<Integer>();
            int minutesOfMarch = toInt(editMarch);
            int minutesOfRun = toInt(editRun);
            int numberOfRepeats = toInt(editRepeats);
            if (isPositive(minutesOfMarch, minutesOfRun, numberOfRepeats)) {
                for (int i = 0; i < numberOfRepeats; i++) {
                    list.add(minutesOfMarch);
                    list.add(minutesOfRun);
                }
                return list;
            } else {
                Toast.makeText(this, getString(R.string.variables_need_to_be_positive), Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return null;
    }

    private void makeSchedule() {
        List<Integer> list = makeList();
        if (list == null)
            Toast.makeText(this, getString(R.string.fill_data), Toast.LENGTH_SHORT).show();
        else {
            alarmManager = new AlarmManager[list.size()];
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < list.size(); i++) {
                cal.add(Calendar.MINUTE, list.get(i));
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra(AlarmReceiver.ACTION_ALARM, AlarmReceiver.ACTION_ALARM);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, 0);
                alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager[i].set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                intentArray.add(pendingIntent);
            }
            int times = list.size();
            preferencesEditor.putInt(ALARMS, times);
            preferencesEditor.commit();
        }
    }

    private void deleteSchedule() {
        if (intentArray.size() > 0) {
            for (int i = 0; i < intentArray.size(); i++) {
                alarmManager[i].cancel(intentArray.get(i));
            }
        }
        intentArray.clear();
        preferencesEditor.putInt(ALARMS, 0);
        preferencesEditor.commit();
    }

    private boolean isInt(EditText editText) {
        try {
            toInt(editText);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (java.lang.NullPointerException l) {
            l.printStackTrace();
            return false;
        }
    }

    private int toInt(EditText editText) {
        return Integer.parseInt(editText.getText().toString());
    }

    private boolean isPositive(int minutesOfMarch, int minutesOfRun, int numberOfRepeats) {
        return minutesOfMarch > 0 && minutesOfRun > 0 && numberOfRepeats > 0;
    }

    private void setM(EditText editText, Button btn) {
        int tmp = toInt(editText);
        if (tmp > 0) {
            editText.setText(getString(R.string.blank) + --tmp);
            if (tmp == 0) btn.setEnabled(false);
        } else btn.setEnabled(false);
    }

    private void setP(EditText editText, Button btn) {
        int tmp = toInt(editText);
        editText.setText(getString(R.string.blank) + ++tmp);
        if (!btn.isEnabled()) btn.setEnabled(true);
    }

    private boolean isWorking() {
        return preferences.getInt(ALARMS, 0) != 0;
    }
}
