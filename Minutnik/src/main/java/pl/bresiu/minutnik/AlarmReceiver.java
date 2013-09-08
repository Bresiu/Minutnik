package pl.bresiu.minutnik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    public static String ACTION_ALARM = "com.alarammanager.alaram";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Interwał", Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getExtras();
        String action = bundle.getString(ACTION_ALARM);
        if (action.equals(ACTION_ALARM)) {
            Intent inService = new Intent(context, TaskService.class);
            context.startService(inService);
        } else {
            Toast.makeText(context, "Else loop", Toast.LENGTH_SHORT).show();
        }
    }
}
