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

public class MainActivity extends Activity implements View.OnClickListener {
    final String ALARMS = "alarms";
    final String PREFERENCES_NAME = "preferences";
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;
    EditText marsz;
    EditText bieg;
    EditText powtorzenia;
    Button startstop;
    Button zeruj;
    ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
    AlarmManager[] alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        preferencesEditor = preferences.edit();
        setWidgets();
    }

    public void setWidgets() {
        marsz = (EditText) findViewById(R.id.minuty_marszu);
        bieg = (EditText) findViewById(R.id.minuty_biegu);
        powtorzenia = (EditText) findViewById(R.id.liczba_powtorzen);
        zeruj = (Button) findViewById(R.id.zero);
        startstop = (Button) findViewById(R.id.startstop);
        startstop.setText((isWorking()) ? "STOP" : "START");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startstop:
                if (isWorking()) {
                    preferencesEditor.putInt(ALARMS, 0);
                    startstop.setText("START");
                    deleteSchedule();
                } else {
                    startstop.setText("STOP");
                    makeSchedule();
                }
                break;
            case R.id.zero:
                marsz.setText("0");
                bieg.setText("0");
                powtorzenia.setText("0");
                break;
            case R.id.button:
                if (isInt(marsz)) {
                    setM(marsz);
                }
                break;
            case R.id.button2:
                if (isInt(marsz)) {
                    setP(marsz);
                }
                break;
            case R.id.button3:
                if (isInt(bieg)) {
                    setM(bieg);
                }
                break;
            case R.id.button4:
                if (isInt(bieg)) {
                    setP(bieg);
                }
                break;
            case R.id.button5:
                if (isInt(powtorzenia)) {
                    setM(powtorzenia);
                }
                break;
            case R.id.button6:
                if (isInt(powtorzenia)) {
                    setP(powtorzenia);
                }
                break;
        }
    }

    private boolean isInt(EditText editText) {
        try {
            Integer.parseInt(editText.getText().toString());
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (java.lang.NullPointerException l) {
            l.printStackTrace();
            return false;
        }
    }

    private void setM(EditText editText) {
        int tmp = Integer.parseInt(editText.getText().toString());
        if (tmp > 0) {
            editText.setText("" + --tmp);
        }
    }

    private void setP(EditText editText) {
        int tmp = Integer.parseInt(editText.getText().toString());
        editText.setText("" + ++tmp);
    }

    public boolean isWorking() {
        return preferences.getInt(ALARMS, 0) != 0;
    }

    public List<Integer> makeList() {
        List<Integer> list = new ArrayList<Integer>();

        int numberOfRepeats = 0;
        int minutesOfMarch = 0;
        int minutesOfRun = 0;
        //TODO
        if (powtorzenia.getText().toString().matches("") ||
                marsz.getText().toString().matches("") ||
                bieg.getText().toString().matches("")) {
            return null;
        } else {
            try {
                numberOfRepeats = Integer.parseInt(powtorzenia.getText().toString());
                minutesOfMarch = Integer.parseInt(marsz.getText().toString());
                minutesOfRun = Integer.parseInt(bieg.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            } catch (java.lang.NullPointerException l) {
                l.printStackTrace();
                return null;
            }
            for (int i = 0; i < numberOfRepeats; i++) {
                list.add(minutesOfMarch);
                list.add(minutesOfRun);
            }
            return list;
        }
    }

    public void makeSchedule() {
        List<Integer> list = makeList();
        if (list == null) {
            Toast.makeText(this, "Wprowadź wszystkie dane!", Toast.LENGTH_SHORT).show();
            preferencesEditor.putInt(ALARMS, 0);
            startstop.setText("START");
            deleteSchedule();
        } else {
            deleteSchedule();
            alarmManager = new AlarmManager[list.size()];
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < list.size(); i++) {
                cal.add(Calendar.SECOND, list.get(i));
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

    public void deleteSchedule() {
        if (intentArray.size() > 0) {
            for (int i = 0; i < intentArray.size(); i++) {
                alarmManager[i].cancel(intentArray.get(i));
            }
        }
        intentArray.clear();
        preferencesEditor.putInt(ALARMS, 0);
        preferencesEditor.commit();
    }
}
